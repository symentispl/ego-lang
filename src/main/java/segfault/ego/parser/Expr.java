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
import static segfault.ego.lexer.Token.number;
import static segfault.ego.lexer.Token.string;

public interface Expr {

    public static ListExpr listExpr(Expr... exprs) {
        return new ListExpr(asList(exprs));
    }

    public static Expr atomExpr(String value) {
        return new AtomExpr(atom(value));
    }

    public static Expr stringLiteralExpr(String value) {
        return new StringLiteralExpr(string(value));
    }

    public static Expr numberLiteralExpr(int value) {
        return new NumberLiteralExpr(number(String.valueOf(value)));
    }

    public static Expr numberLiteralExpr(double value) {
        return new NumberLiteralExpr(number(String.valueOf(value)));
    }

    public static Expr numberLiteralExpr(float value) {
        return new NumberLiteralExpr(number(String.valueOf(value)));
    }

    void accept(Visitor visitor);

}