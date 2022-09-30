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

import static segfault.ego.lexer.CharacterClasses.CLOSING_BRACKET;
import static segfault.ego.lexer.CharacterClasses.OPENING_BRACKET;

public record Token(Kind kind, String value) {

    public static Token openingBracket() {
        return new Token(Kind.OPENING_BRACKET, Character.toString(OPENING_BRACKET));
    }

    public static Token closingBracket() {
        return new Token(Kind.CLOSING_BRACKET, Character.toString(CLOSING_BRACKET));
    }

    public static Token eof() {

        return new Token(Kind.EOF, null);
    }

    public static Token atom(String value) {
        return new Token(Kind.ATOM, value);
    }

    public static Token string(String str) {
        return new Token(Kind.STRING, str);
    }

    public static Token number(String value) {
        return new Token(Kind.NUMBER, value);
    }
}
