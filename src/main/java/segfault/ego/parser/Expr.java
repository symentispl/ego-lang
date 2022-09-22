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

import static java.util.Arrays.asList;
import static segfault.ego.lexer.Token.atom;
import static segfault.ego.lexer.Token.string;

import java.lang.reflect.Type;
import java.util.Optional;

public interface Expr<T extends Visitor> {

    static ListLiteral listExpr(Expr... exprs) {
        return new ListLiteral(asList(exprs));
    }

    static AtomLiteral atomExpr(String value) {
        return new AtomLiteral(atom(value));
    }

    static StringLiteral stringLiteralExpr(String value) {
        return new StringLiteral(string(value));
    }

    /**
     * Returns non-empty optional if expression is pair
     */
    static <F, S> Optional<Pair<F, S>> pairOf(Expr expr, Class<F> first, Class<S> second) {
        return Optional.of(expr).filter(e -> e instanceof ListLiteral).map(e -> ListLiteral.class.cast(e))
                .filter(e -> e.exprs().size() == 2)
                .filter(e -> first.isInstance(e.exprs().get(0)) && second.isInstance(e.exprs().get(1)))
                .map(e -> new Pair<F, S>((F) e.exprs().get(0), (S) e.exprs().get(1)));
    }

    Type type();

    void accept(T visitor);

}