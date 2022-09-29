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

import static java.util.stream.Collectors.toList;

import java.util.List;
import segfault.ego.types.Atom;

public class InterpretEgoAlg implements EgoAlg<List<?>, Atom, Number, String> {

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
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }
}
