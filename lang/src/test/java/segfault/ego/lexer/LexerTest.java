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
package segfault.ego.lexer;

import static org.assertj.core.api.Assertions.assertThat;
import static segfault.ego.lexer.Token.atom;
import static segfault.ego.lexer.Token.closingBracket;
import static segfault.ego.lexer.Token.eof;
import static segfault.ego.lexer.Token.number;
import static segfault.ego.lexer.Token.openingBracket;
import static segfault.ego.lexer.Token.string;

import org.junit.jupiter.api.Test;

public class LexerTest {

    Lexer lexer = new Lexer();

    @Test
    public void tokenize_empty_expression() {
        var tokens = lexer.tokenize("");

        assertThat(tokens)
            .containsExactly(
                eof());
    }

    @Test
    public void tokenize_atom() {
        var tokens = lexer.tokenize("hello");

        assertThat(tokens)
            .containsExactly(
                atom("hello"),
                eof());
    }

    @Test
    public void tokenize_number() {
        var tokens = lexer.tokenize("129");

        assertThat(tokens)
            .containsExactly(
                number("129"),
                eof());
    }

    @Test
    public void tokenize_pozitive_number() {
        var tokens = lexer.tokenize("+129");

        assertThat(tokens)
            .containsExactly(
                number("129"),
                eof());
    }

    @Test
    public void tokenize_negative_number() {
        var tokens = lexer.tokenize("-129");

        assertThat(tokens)
            .containsExactly(
                number("-129"),
                eof());
    }

    @Test
    public void tokenize_decimal_number() {
        var tokens = lexer.tokenize("129.0");

        assertThat(tokens)
            .containsExactly(
                number("129.0"),
                eof());
    }

    @Test
    public void tokenize_pozitive_decimal_number() {
        var tokens = lexer.tokenize("+129.0");

        assertThat(tokens)
            .containsExactly(
                number("129.0"),
                eof());
    }

    @Test
    public void tokenize_negative_decimal_number() {
        var tokens = lexer.tokenize("-129.0");

        assertThat(tokens)
            .containsExactly(
                number("-129.0"),
                eof());
    }

    @Test
    public void tokenize_number_and_closing_bracket() {
        var tokens = lexer.tokenize("129)");

        assertThat(tokens)
            .containsExactly(
                number("129"),
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_number_within_list() {
        var tokens = lexer.tokenize("(129)");

        assertThat(tokens)
            .containsExactly(
                openingBracket(),
                number("129"),
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_number_concat_with_atom() {
        var tokens = lexer.tokenize("129test");

        assertThat(tokens)
            .containsExactly(
                atom("129test"),
                eof());
    }

    @Test
    public void tokenize_number_with_sign_between_digits() {
        var tokens = lexer.tokenize("1+29");

        assertThat(tokens)
            .containsExactly(
                atom("1+29"),
                eof());
    }

    @Test
    public void tokenize_expression() {
        var tokens = lexer.tokenize("( 129test 129 test )");

        assertThat(tokens)
            .containsExactly(
                openingBracket(),
                atom("129test"),
                number("129"),
                atom("test"),
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_string() {
        var tokens = lexer.tokenize("""
            "hello world"
        """);

        assertThat(tokens)
            .containsExactly(
                string("hello world"),
                eof());
    }

    @Test
    public void tokenize_empty_list() {
        var tokens = lexer.tokenize("()");

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                closingBracket(), 
                eof());
    }

    @Test
    public void tokenize_multiline_empty_list() {
        var tokens = lexer.tokenize("""
            (

            )
        """);

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                closingBracket(), 
                eof());
    }

    @Test
    public void tokenize_multiline_empty_list_with_comment() {
        var tokens = lexer.tokenize("""
            (
                ; do nothing
            )
        """);

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                closingBracket(), 
                eof());
    }

    @Test
    public void tokenize_opening_bracket() {
        var tokens = lexer.tokenize("(");

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                eof());
    }
    
    @Test
    public void tokenize_closing_bracket() {
        var tokens = lexer.tokenize(")");
        
        assertThat(tokens)
            .containsExactly(
                closingBracket(), 
                eof());
    }

    @Test
    public void tokenize_unclosed_list() {
        var tokens = lexer.tokenize("( hi");

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("hi"),
                eof());
    }

    @Test
    public void tokenize_unopened_list() {
        var tokens = lexer.tokenize("""
            print "hello world" )
        """);

        assertThat(tokens)
            .containsExactly(
                atom("print"),
                string("hello world"),
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_atoms_within_list() {
        var tokens = lexer.tokenize("(print Hi)");

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("print"), 
                atom("Hi"), 
                closingBracket(), 
                eof());
    }

    @Test
    public void tokenize_atom_and_string() {
        var tokens = lexer.tokenize("""
                (print "Hello world")""");

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("print"), 
                string("Hello world"), 
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_complex_expression() {
        var tokens = lexer.tokenize("""
                (print "4+5" (+ 4 5))
            """);

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("print"), 
                string("4+5"), 
                openingBracket(),
                atom("+"),
                number("4"),
                number("5"),
                closingBracket(),
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_expression_with_comment() {
        var tokens = lexer.tokenize("""
                (print "Hello Moto!") ; this is comment
            """);

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("print"), 
                string("Hello Moto!"), 
                closingBracket(),
                eof());
    }

    @Test
    public void tokenize_multiline_expression_with_comment() {
        var tokens = lexer.tokenize("""
                (print  ; this is comment
                    (greet "Jarek"))
            """);

        assertThat(tokens)
            .containsExactly(
                openingBracket(), 
                atom("print"), 
                openingBracket(),
                atom("greet"),
                string("Jarek"), 
                closingBracket(),
                closingBracket(),
                eof());
    }

}