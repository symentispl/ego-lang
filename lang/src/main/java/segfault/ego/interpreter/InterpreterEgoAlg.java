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

import java.util.List;
import segfault.ego.parser.AtomLiteral;
import segfault.ego.parser.EgoAlg;
import segfault.ego.parser.Expr;
import segfault.ego.parser.FunCall;
import segfault.ego.parser.ListLiteral;
import segfault.ego.parser.NumberLiteral;
import segfault.ego.parser.Scope;
import segfault.ego.parser.StringLiteral;
import segfault.ego.types.Atom;

public class InterpreterEgoAlg implements EgoAlg<List<?>, Atom, Number, String, Object> {

    private final Scope scope;

    public InterpreterEgoAlg(Scope scope) {
        this.scope = scope;
    }

    @Override
    public List<?> listLiteral(ListLiteral literal) {

        return literal.exprs().stream().map(this::eval).collect(toList());
    }

    public Number numberLiteral(NumberLiteral n) {
        return n.number();
    }

    public Atom atomLiteral(AtomLiteral a) {
        return new Atom(a.atom());
    }

    public String stringLiteral(StringLiteral s) {
        return s.string();
    }

    public Object eval(Expr expr) {
        return switch (expr) {
            case AtomLiteral a -> atomLiteral(a);
            case NumberLiteral n -> numberLiteral(n);
            case StringLiteral s -> stringLiteral(s);
            case ListLiteral l -> listLiteral(l);
            case FunCall f -> funCall(f);
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }

    @Override
    public Object funCall(FunCall funCall) {
        var parameters = funCall.parameters().stream().map(this::eval).toList();
        try {
            return funCall.symbol().mh().invokeWithArguments(parameters);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
