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

import java.util.ArrayList;
import java.util.List;
import segfault.ego.lexer.Kind;
import segfault.ego.lexer.Token;

public class Parser {

    public Expr parse(List<Token> tokens) {
        var iterator = new PushbackIterator<>(tokens.iterator());
        return exprRule(iterator);
    }

    private Expr listExprRule(PushbackIterator<Token> tokens) {
        var elements = new ArrayList<Expr>();

        while (tokens.hasNext()) {
            var token = tokens.next();
            if (token.kind() == Kind.CLOSING_BRACKET) {
                return new ListLiteral(elements);
            }
            tokens.pushback(token);
            elements.add(exprRule(tokens));
        }
        throw new IllegalStateException();
    }

    private Expr exprRule(PushbackIterator<Token> tokens) {
        while (tokens.hasNext()) {
            var token = tokens.next();
            if (token.kind() == Kind.OPENING_BRACKET) {
                return listExprRule(tokens);
            }

            if (token.kind() == Kind.STRING) {
                return new StringLiteral(token);
            }

            if (token.kind() == Kind.ATOM) {
                return new AtomLiteral(token);
            }

            if (token.kind() == Kind.NUMBER) {
                return new NumberLiteral(token);
            }

            if (token.kind() == Kind.EOF) {
                // no op;
            }
        }
        throw new IllegalStateException();
    }
}
