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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import segfault.ego.lexer.Lexer;
import segfault.ego.lexer.Token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static segfault.ego.parser.Expr.*;

import java.util.List;

public class ParserTest {
    private Lexer lexer;
    private Parser parser;

    @BeforeEach
    void beforeAll(){
        lexer = new Lexer();
        parser = new Parser();
    }

    @Test
    void parse_string_literal() {
        var expr = parser.parse(lexer.tokenize("""
        "Hello Dave"
        """));

        assertThat(expr).isEqualTo(
            stringLiteralExpr("Hello Dave"));
    }

    @Test
    void parse_number_literal() {
        var expr = parser.parse(lexer.tokenize("8"));

        assertThat(expr).isEqualTo(
            numberLiteralExpr(8));
    }

    @Test
    void parse_empty_list() {
        var expr = parser.parse(lexer.tokenize("()"));

        assertThat(expr).isEqualTo(
            listExpr());
    }

    @Test
    void parse_list_of_strings() {
        var expr = parser.parse(lexer.tokenize("""
        ("Hello Moto" "Hello Dave" "Goodnight")
        """));

        assertThat(expr).isEqualTo(
            listExpr(
                stringLiteralExpr("Hello Moto"),
                stringLiteralExpr("Hello Dave"),
                stringLiteralExpr("Goodnight")));
    }

    @Test
    void parse_list_of_numbers() {
        var expr = parser.parse(lexer.tokenize("(1 2.5 -3)"));

        assertThat(expr).isEqualTo(
            listExpr(
                numberLiteralExpr(1),
                numberLiteralExpr(2.5),
                numberLiteralExpr(-3)));
    }

    @Test
    void parse_list_of_atoms() {
        var expr = parser.parse(lexer.tokenize("(my luckily atom)"));

        assertThat(expr).isEqualTo(
            listExpr(
                atomExpr("my"),
                atomExpr("luckily"),
                atomExpr("atom")));
    }
    
    @Test
    void parse_mixed_list() {
        var expr = parser.parse(lexer.tokenize("""
        (atom "Hello Dave" -12.3)
        """));

        assertThat(expr).isEqualTo(
            listExpr(
                atomExpr("atom"),
                stringLiteralExpr("Hello Dave"),
                numberLiteralExpr(-12.3)));
    }

    @Test
    void parse_empty_nested_list(){
        var expr = parser.parse(lexer.tokenize("(())"));

        assertThat(expr).isEqualTo(
            listExpr(
                listExpr()));
    }

    @Test
    void parse_nested_list_with_single_atom(){
        var expr = parser.parse(lexer.tokenize("((atom))"));

        assertThat(expr).isEqualTo(
            listExpr(
                listExpr(
                    atomExpr("atom"))));
    }


    @Test
    void parse_list_of_lists(){
        var expr = parser.parse(lexer.tokenize("((atom)())"));

        assertThat(expr).isEqualTo(
            listExpr(
                listExpr(
                    atomExpr("atom")), 
                listExpr()));
    }

    @Test
    void parse_list_of_atom_and_nested_list(){
        var expr = parser.parse(lexer.tokenize("""
                ((atom (help "Me")))
                """));

        assertThat(expr).isEqualTo(
            listExpr(
                listExpr(
                    atomExpr("atom"), 
                    listExpr(atomExpr("help"), 
                    stringLiteralExpr("Me")))));
    }

    @Test
    void parse_full_sentence(){
        var expr = parser.parse(lexer.tokenize("""
                (print (greet "Dave"))
                """));

        assertThat(expr).isEqualTo(
            listExpr(
                atomExpr("print"),
                listExpr(
                    atomExpr("greet"), 
                    stringLiteralExpr("Dave"))));
    }

    @Test
    void parse_unclosed_list(){
        List<Token> tokens = lexer.tokenize("(1 2 3");
        
        Throwable thrown = catchThrowable(() -> parser.parse(tokens));

        assertThat(thrown)
            .isInstanceOf(EgoParserException.class)
            .hasMessage("missing closing bracket");
        
    }

    @Test
    void parse_unopened_list(){
        List<Token> tokens = lexer.tokenize("1 2 3)");
        
        Throwable thrown = catchThrowable(() -> parser.parse(tokens));

        assertThat(thrown)
            .isInstanceOf(EgoParserException.class)
            .hasMessage("Unexpected token: Token[kind=NUMBER, value=2]");

    }
}