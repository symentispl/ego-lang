/*
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
package segfault.ego.interpreter;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

import segfault.ego.parser.AtomLiteral;
import segfault.ego.parser.Expr;
import segfault.ego.parser.FunCall;
import segfault.ego.parser.FunDecl;
import segfault.ego.parser.Lambda;
import segfault.ego.parser.ListLiteral;
import segfault.ego.parser.NumberLiteral;
import segfault.ego.parser.ObjectLiteral;
import segfault.ego.parser.ObjectMember;
import segfault.ego.parser.Ref;
import segfault.ego.parser.RewriteVisitor;
import segfault.ego.parser.StringLiteral;
import segfault.ego.parser.ValDecl;
import segfault.ego.symbols.ValSymbol;
import segfault.ego.types.EgoObject;

class InterpreterVisitor implements RewriteVisitor {

    private final Stack<Object> stack = new Stack<>();
    private final Stack<Context> contexts = new Stack<>();

    InterpreterVisitor(GlobalContext context) {
        this.contexts.push(context);
    }

    @Override
    public void visit(AtomLiteral atomLiteral) {
        // here we need to resolve object member access if atom follows this shape
        // obj.field1.field2.field3
        // atomLiteral.token().value().split(".");
        // TODO rewrite it, into MemberAccess expr
        // String[] memberAccess = atomLiteral.token().value().split(".");
        // String root = memberAccess[0];
        // Object object = context.get(root);
        // Arrays.stream(memberAccess).skip(1).collect(Collectors.toList());

        stack.push(currentContext().get(atomLiteral.value()));
    }

    @Override
    public void visit(NumberLiteral numberLiteral) {
        // here we need to resolve object member access if atom follows this shape
        // obj.field1.field2.field3
        // atomLiteral.token().value().split(".");
        // TODO rewrite it, into MemberAccess expr
        // String[] memberAccess = atomLiteral.token().value().split(".");
        // String root = memberAccess[0];
        // Object object = context.get(root);
        // Arrays.stream(memberAccess).skip(1).collect(Collectors.toList());
        stack.push(Integer.valueOf(numberLiteral.token().value()));
    }

    @Override
    public void visit(ListLiteral listExpr) {
        List<Object> list = listExpr.exprs().stream().map(expr -> {
            expr.accept(this);
            return returns();
        }).collect(toList());
        stack.push(list);
    }

    @Override
    public void visit(StringLiteral stringLiteralExpr) {
        stack.push(stringLiteralExpr.token().value());
    }

    Object returns() {
        return stack.pop();
    }

    public void visit(FunCall funCall) {
        // TODO we should resolve it (again MemberAccess)
        var function = currentContext().get(funCall.symbol()); // simplest options is to bind function call to
                                                      // Function<<List<Object>>,Object>,
                                                      // which is bad as it erases information about types, since we
                                                      // type information we could
                                                      // use method handles
        var parametersExpr = funCall.parameters();
        // evaluate parameters
        var parameters = parametersExpr.stream().map(this::evaluateExpr).collect(toList());
        stack.push(((Function<List<Object>, Object>) function).apply(parameters));
    }

    public void visit(ValDecl valDecl) {
        valDecl.expr().accept(this);
        Object exprValue = returns();
        currentContext().set(valDecl.symbol(), exprValue);
        stack.push(exprValue);
    }

    public void visit(ObjectLiteral objectLiteral) {
        var members = new HashMap<String, Object>();
        for (ObjectMember objMember : objectLiteral.members()) {
            objMember.accept(this);
            var objMemberValue = returns();
            members.put(objMember.name(), objMemberValue);
        }
        stack.push(new EgoObject(members));
    }

    public void visit(ObjectMember objectMember) {
        // TODO if expr is FunDecl, it should just return EgoFunction, not eval it
        objectMember.expr().accept(this);
    }

    public void visit(FunDecl funDecl) {
        Expr body = funDecl.body();

        Function<List<Object>,Object> f = parameters -> {
            contexts.push(new LocalContext(currentContext()));
            var scope = funDecl.symbol().type().parameters();
            for(int i=0;i<scope.size();i++){
                currentContext().set(scope.get(i), parameters.get(i));
            }       
            body.accept(this);
            contexts.pop();
            return returns();
        };
        currentContext().set(funDecl.symbol(), f);

        stack.push(f);

    }

    public void visit(Ref ref) {
        stack.push(currentContext().get(ref.symbol()));
    }

    private Object evaluateExpr(Expr expr) {
        expr.accept(this);
        return returns();
    }

    @Override
    public void visit(Lambda lambda) {
        Expr body = lambda.body();

        Function<List<Object>,Object> f = parameters -> {
            contexts.push(new LocalContext(currentContext()));
            List<ValSymbol> scope = lambda.paramsDecl().stream().map(decl -> new ValSymbol(decl.name(), decl.type())).collect(toList());
            for(int i=0;i<scope.size();i++){
                currentContext().set(scope.get(i), parameters.get(i));
            }       
            body.accept(this);
            contexts.pop();
            return returns();
        };

        stack.push(f);
    }

    private Context currentContext(){
        return contexts.peek();
    }
}
