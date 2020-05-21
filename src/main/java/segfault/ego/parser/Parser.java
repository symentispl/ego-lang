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

import java.util.ArrayList;
import java.util.List;

import segfault.ego.lexer.Kind;
import segfault.ego.lexer.Token;

public class Parser {

    public Expr parse(List<Token> tokens) {
        var iterator = new PushbackIterator<>(tokens.iterator());
        var expr = exprRule(iterator);
        if(iterator.hasNext()){
            throw new EgoParserException("Unexpected token: " + iterator.next());
        }
        return expr;
    }

    private Expr listExprRule(PushbackIterator<Token> tokens) {
        var elements = new ArrayList<Expr>();

        while (tokens.hasNext()) {
            var token = tokens.next();
            if (token.kind() == Kind.CLOSING_BRACKET) {
                return new ListExpr(elements);
            }
            tokens.pushback(token);
            var expr = exprRule(tokens);
            if (expr != null) {
                elements.add(expr);
            }
        }
        throw new EgoParserException("missing closing bracket");
    }

    private Expr exprRule(PushbackIterator<Token> tokens) {
        Token token = null;
        while (tokens.hasNext()) {
            token = tokens.next();
            if (token.kind() == Kind.OPENING_BRACKET) {
                return listExprRule(tokens);
            }

            if (token.kind() == Kind.STRING) {
                return new StringLiteralExpr(token);
            }

            if (token.kind() == Kind.ATOM) {
                return new AtomExpr(token);
            }

            if (token.kind() == Kind.NUMBER) {
                return new NumberLiteralExpr(token);
            }

            if (token.kind() == Kind.EOF) {
                return null;
            }
        }
        throw new EgoParserException("Unexpected token: " + token);
    }
}