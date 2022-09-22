/**
 * Copyright © 2020 Segfault (wiktor@segfault.events,jarek@segfault.events)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package segfault.ego.parser;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

import segfault.ego.symbols.FunctionSymbol;
import segfault.ego.symbols.Symbol;
import segfault.ego.symbols.TypeSymbol;
import segfault.ego.symbols.ValSymbol;
import segfault.ego.types.EgoObjectType;
import segfault.ego.types.FunctionType;
import segfault.ego.types.Member;
import segfault.ego.types.ParameterSymbol;

public class RewriteAST implements Visitor {

    private final Stack<Scope> scopes = new Stack<>();
    private final Stack<Expr> stack = new Stack<>();

    public RewriteAST(GlobalScope scope) {
        scopes.push(scope);
    }

    @Override
    public void visit(AtomLiteral atomExpr) {
        Symbol symbol = currentScope().resolve(atomExpr.value());
        if (symbol!=null) {
            stack.push(new Ref( symbol ));
        } else {
            throw new IllegalArgumentException("unresolved ref");
        }
    }

    @Override
    public void visit(StringLiteral expr) {
        stack.push(expr);
    }

    @Override
    public void visit(NumberLiteral expr) {
        stack.push(expr);
    }

    @Override
    public void visit(ListLiteral listExpr) {
        var rewritten = listExpr
                .firstOf(AtomLiteral.class)
                .map(rewriteSpecialForm(listExpr))
                .orElseGet(rewriteList(listExpr));
        stack.push(rewritten);
    }

    /** rewrite all elements of the list */
    private Supplier<Expr<Visitor>> rewriteList(ListLiteral listExpr) {
        return () -> {
            var list = new ArrayList<Expr>();
            for (Expr e : listExpr.exprs()) {
                e.accept(this);
                list.add(returns());
            }
            return new ListLiteral(list);
        };
    }

    private Function<AtomLiteral, Expr> rewriteSpecialForm(ListLiteral listExpr) {
        return (expr) -> switch (expr.token().value()) {
            case "val" -> valDecl(listExpr.tail());
            case "object" -> objectDecl(listExpr.tail());
            case "fun" -> funDecl(listExpr.tail());
            case "lambda" -> lambda(listExpr.tail());
            default -> funCall(listExpr);
        };
    }

    private FunDecl funDecl(List<Expr> exprs) {
        // function name
        var name = ListLiteral.firstOf(exprs, AtomLiteral.class).map(literal -> literal.token().value())
                .orElseThrow(() -> new IllegalStateException("unexpected type wanted atom"));
        // parameters
        List<Expr> parametersDeclListExpr = ListLiteral.tail(exprs);
        var listOfParamDecl = ListLiteral.firstOf(parametersDeclListExpr, ListLiteral.class)
                .orElseThrow(() -> new IllegalStateException("expected list of function parameters declaration"))
                .exprs().stream().map(this::paramDecl).collect(toList());
        // return type
        List<Expr> returnTypeExpr = ListLiteral.tail(parametersDeclListExpr);
        Type returnType = ListLiteral.firstOf(returnTypeExpr, ListLiteral.class).map(this::returnType)
                .orElseThrow(() -> new IllegalStateException("function return type"));
        // function body
        LocalScope functionScope = new LocalScope(currentScope());
        List<ParameterSymbol> parameterSymbols = listOfParamDecl.stream()
                .map(decl -> new ParameterSymbol(decl.name(), decl.type())).collect(toList());
        FunctionSymbol symbol = new FunctionSymbol(name, new FunctionType(parameterSymbols, returnType));
        symbol.type().parameters().forEach(functionScope::define);
        scopes.push(functionScope);
        ListLiteral.tail(returnTypeExpr).get(0).accept(this);
        Expr body = returns();
        scopes.pop();
        FunDecl funDecl = new FunDecl(symbol, body);
        currentScope().define(symbol);
        return funDecl;
    }

    private Lambda lambda(List<Expr> exprs) {
        // parameters
        List<Expr> parametersDeclListExpr = exprs;
        var listOfParamDecl = ListLiteral.firstOf(parametersDeclListExpr, ListLiteral.class)
                .orElseThrow(() -> new IllegalStateException("expected list of function parameters declaration"))
                .exprs().stream().map(this::paramDecl).collect(toList());
        // return type
        List<Expr> returnTypeExpr = ListLiteral.tail(parametersDeclListExpr);
        Type returnType = ListLiteral.firstOf(returnTypeExpr, ListLiteral.class).map(this::returnType)
                .orElseThrow(() -> new IllegalStateException("function return type"));
        // function body
        LocalScope functionScope = new LocalScope(currentScope());
        listOfParamDecl.stream().map(decl -> new ValSymbol(decl.name(), decl.type())).forEach(functionScope::define);
        scopes.push(functionScope);
        ListLiteral.tail(returnTypeExpr).get(0).accept(this);
        Expr body = returns();
        scopes.pop();
        Lambda lambda = new Lambda(listOfParamDecl, returnType, body);
        return lambda;
    }

    private Type returnType(ListLiteral expr) {
        return Expr.pairOf(expr, AtomLiteral.class, AtomLiteral.class).map(pair -> {
            String s = pair.first().value();
            // resolve return type
            String typeToken = pair.second().token().value();
            Symbol typeSymbol = currentScope().resolve(typeToken);
            if (!(typeSymbol instanceof TypeSymbol)) {
                throw new IllegalStateException();
            }

            return typeSymbol.type();
        }).orElseThrow(() -> new IllegalStateException());
    }

    private ParameterDecl paramDecl(Expr expr) {
        return Expr.pairOf(expr, AtomLiteral.class, AtomLiteral.class).map(pair -> {
            // resolve parameter type
            Symbol typeSymbol = currentScope().resolve(pair.second().token().value());
            if (!(typeSymbol instanceof TypeSymbol)) {
                throw new IllegalStateException();
            }
            return new ParameterDecl(pair.first().token().value(), ((TypeSymbol) typeSymbol).type());
        }).orElseThrow(() -> new IllegalStateException());
    }

    private FunCall funCall(ListLiteral listLiteral) {
        var funName = listLiteral
                .firstOf(AtomLiteral.class)
                .orElseThrow(() -> new IllegalStateException("expected atom"));

        var funParameters = listLiteral
                .tail()
                .stream()
                .map(expr -> {
                    expr.accept(this);
                    return returns();
            }).collect(toList());

        var symbol = currentScope().resolve(funName.value());
        if (symbol instanceof FunctionSymbol || (symbol instanceof ValSymbol && symbol.type().equals(Function.class))) {

            // TODO static check if resolved function symbol matches function call site
            return new FunCall(symbol, funParameters);
        }
        throw new IllegalArgumentException(format("unresolved symbol %s - %s", funName.value(), symbol));
    }

    private Scope currentScope() {
        return scopes.peek();
    }

    /**
     * expects list of pairs (atom expr)
     */
    private ObjectLiteral objectDecl(List<Expr> tail) {
        // get object literal members
        var objectMembers = tail.stream().map(this::objectMember).collect(toList());
        // and now inference object type
        var type = new EgoObjectType(objectMembers.stream()
                .map(objectMember -> new Member(objectMember.name(), objectMember.type())).collect(toList()));
        return new ObjectLiteral(objectMembers, type);
    }

    /** expects pair (atom expr) */
    private ObjectMember objectMember(Expr expr) {
        return Expr.pairOf(expr, AtomLiteral.class, Expr.class)
                .map(pair -> new ObjectMember(pair.first().value(), pair.second()))
                .orElseThrow(() -> new IllegalArgumentException("invalid object literal"));
    }

    /* (val name expr) */
    private ValDecl valDecl(List<Expr> exprs) {

        if (exprs.size() != 2) {
            throw new IllegalStateException("invalid special form `val`");
        }

        return ListLiteral.firstOf(exprs, AtomLiteral.class).map(atomExpr -> {
            var valueExpr = exprs.get(1);
            valueExpr.accept(this);
            var value = returns();
            var scope = currentScope();
            var name = atomExpr.token().value();
            var valSymbol = new ValSymbol(name, value.type());
            var valDecl = new ValDecl(valSymbol, value);
            scope.define(valSymbol);
            return valDecl;
        }).orElseThrow(() -> new IllegalStateException("value declaration should start with atom"));

    }

    Expr returns() {
        return stack.pop();
    }

}
