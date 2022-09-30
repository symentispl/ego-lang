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

import segfault.ego.symbols.FunctionSymbol;

public class RewritingEgoAlg implements SexprAlg<Expr, AtomLiteral, NumberLiteral, StringLiteral> {

    private final Scope scope;

    public RewritingEgoAlg(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Expr listLiteral(ListLiteral literal) {
        return literal.firstOf(AtomLiteral.class)
                .<Expr>map(e -> new FunCall(
                        (FunctionSymbol) scope.resolve(e.atom()),
                        literal.tail().stream().map(this::eval).toList()))
                .orElse(new ListLiteral(literal.exprs().stream().map(this::eval).toList()));
    }

    @Override
    public AtomLiteral atomLiteral(AtomLiteral a) {
        return a;
    }

    @Override
    public NumberLiteral numberLiteral(NumberLiteral n) {
        return n;
    }

    @Override
    public StringLiteral stringLiteral(StringLiteral s) {
        return s;
    }

    public Expr eval(Expr expr) {
        return switch (expr) {
            case AtomLiteral a -> atomLiteral(a);
            case NumberLiteral n -> numberLiteral(n);
            case StringLiteral s -> stringLiteral(s);
            case ListLiteral l -> listLiteral(l);
            default -> throw new IllegalStateException("Unexpected value: " + expr);
        };
    }
}
