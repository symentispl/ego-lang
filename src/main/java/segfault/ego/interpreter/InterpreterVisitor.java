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
package segfault.ego.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import segfault.ego.parser.AtomExpr;
import segfault.ego.parser.Expr;
import segfault.ego.parser.ListExpr;
import segfault.ego.parser.StringLiteralExpr;
import segfault.ego.parser.Visitor;

public class InterpreterVisitor implements Visitor {

    private final Stack<Object> stack = new Stack<>();

    private final Map<String, Function<List<Object>, Object>> functions = Map.of(
        "print", parameters -> {
                    System.out.println(parameters.stream().map(Object::toString).collect(Collectors.joining(" ")));
                    return null;
                });

    @Override
    public void visit(AtomExpr atomExpr) {

    }

    @Override
    public void visit(ListExpr listExpr) {
        stack.push(listExpr.firstOf(AtomExpr.class).map(this::resolveFunction).get().apply(listExpr.tail()));
    }

    @Override
    public void visit(StringLiteralExpr stringLiteralExpr) {
        stack.push(stringLiteralExpr.token().value());
    }

    Object returns() {
        return stack.pop();
    }

    Function<List<Expr>, Object> resolveFunction(AtomExpr atomExpr) {

        Function<List<Object>, Object> function = functions.get(atomExpr.token().value());

        return parameters -> {
            var parameterValues = new ArrayList<Object>();
            for (Expr parameter : parameters) {
                parameter.accept(this);
                parameterValues.add(returns());
            }
            return function.apply(parameterValues);
        };
    }

}
