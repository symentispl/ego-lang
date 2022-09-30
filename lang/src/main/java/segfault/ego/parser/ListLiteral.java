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
package segfault.ego.parser;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ListLiteral(List<Expr> exprs) implements Expr<Visitor> {

    /**
     * Returns non-empty optional if first element is of exprClass type
     */
    static <T extends Expr> Optional<T> firstOf(List<Expr> exprs, Class<T> exprClass) {
        return exprs.stream().findFirst().filter(f -> exprClass.isInstance(f)).map(f -> exprClass.cast(f));
    }

    /**
     * Returns non-empty optional if expr is ListLiteral
     */
    static Optional<ListLiteral> of(Expr expr) {
        return Optional.of(expr).filter(e -> ListLiteral.class.isInstance(e)).map(e -> ListLiteral.class.cast(e));
    }

    /**
     * Returns tail of list
     */
    static List<Expr> tail(List<Expr> exprs) {
        return exprs.isEmpty() ? Collections.emptyList() : exprs.subList(1, exprs.size());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public Type type() {
        return List.class;
    }

    public List<Expr> tail() {
        return tail(exprs);
    }

    <T extends Expr> Optional<T> firstOf(Class<T> exprClass) {
        return firstOf(exprs, exprClass);
    }

    /**
     * Returns non-empty optional if list pair expressions
     */
    Optional<Map.Entry<Expr, Expr>> pairOf() {
        return exprs.size() == 2 ? Optional.of(Map.entry(exprs.get(0), exprs.get(1))) : Optional.empty();
    }

    /**
     * Returns non-empty optional if list pair expressions of provided types
     */
    <K extends Expr, V extends Expr> Optional<Map.Entry<K, V>> pairOf(Class<K> first, Class<V> second) {
        return pairOf().filter(entry -> first.isInstance(entry.getKey()))
                .filter(entry -> second.isInstance(entry.getValue()))
                .map(entry -> Map.entry((K) entry.getKey(), (V) entry.getValue()));
    }
}