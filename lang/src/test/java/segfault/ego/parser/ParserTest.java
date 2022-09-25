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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import segfault.ego.lexer.Lexer;

import static org.assertj.core.api.Assertions.assertThat;
import static segfault.ego.parser.Expr.*;

public class ParserTest {

    @Test
    @Disabled(" this test was used for first pass of parser visitor")
    public void parse() {
        var lexer = new Lexer();
        var parser = new Parser(new GlobalScope(new BuiltInScope()));

        var expr = parser.parse(lexer.tokenize("()"));
        assertThat(expr).isEqualTo(listExpr());

        expr = parser.parse(lexer.tokenize("(())"));
        assertThat(expr).isEqualTo(listExpr(listExpr()));

        expr = parser.parse(lexer.tokenize("((atom))"));
        assertThat(expr).isEqualTo(listExpr(listExpr(atomExpr("atom"))));

        expr = parser.parse(lexer.tokenize("((atom))"));
        assertThat(expr).isEqualTo(listExpr(listExpr(atomExpr("atom"))));

        expr = parser.parse(lexer.tokenize("((atom)())"));
        assertThat(expr).isEqualTo(listExpr(listExpr(atomExpr("atom")), listExpr()));

        expr = parser.parse(lexer.tokenize("""
                ((atom (help "Me")))"""));
        assertThat(expr)
                .isEqualTo(listExpr(listExpr(atomExpr("atom"), listExpr(atomExpr("help"), stringLiteralExpr("Me")))));
    }

}