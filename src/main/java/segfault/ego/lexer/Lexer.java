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
package segfault.ego.lexer;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;
import static segfault.ego.lexer.Token.atom;
import static segfault.ego.lexer.Token.closingBracket;
import static segfault.ego.lexer.Token.eof;
import static segfault.ego.lexer.Token.number;
import static segfault.ego.lexer.Token.openingBracket;
import static segfault.ego.lexer.Token.string;
import static segfault.ego.lexer.CharacterClasses.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public List<Token> tokenize(String s) {
        var tokens = new ArrayList<Token>();
        var reader = new PushbackReader(new StringReader(s), 64);

        int ch;
        try {
            while (isNotEof(ch = reader.read())) {

                // comment start
                if (isCommentStart(ch)) {
                    int tmp;
                    while ((tmp = reader.read()) != '\n' && tmp != '\r' && isNotEof(tmp)) {
                        // no op
                    }
                    if (tmp == '\r') {
                        // skip next
                        tmp = reader.read();
                        if (tmp != '\n') {
                            throw new IllegalStateException(format("unexpected character '%s'", tmp));
                        }
                    }
                    continue;
                }

                if (isOpeningBracket(ch)) {
                    tokens.add(openingBracket());
                    continue;
                }

                if (isClosingBracket(ch)) {
                    tokens.add(closingBracket());
                    continue;
                }

                // looking up for string
                if (isQuote(ch)) {
                    var buffer = new StringBuilder();
                    int tmp;
                    while (!isQuote(tmp = reader.read())) {
                        if (isEof(tmp)) {
                            throw new IllegalStateException("missing closing quote " + buffer.toString());
                        }
                        buffer.appendCodePoint(tmp);
                    }

                    tokens.add(string(buffer.toString()));
                    continue;
                }

                // looking up for number
                if (isDigit(ch) || isSign(ch)) {
                    var buffer = new StringBuilder();
                    int tmp = ch;
                    boolean isNumber = false; // before dot
                    boolean isDecimal = false; // after dot
                    if(isSign(tmp)){
                        buffer.appendCodePoint(tmp);
                        tmp = reader.read();
                    }
                    do {
                        buffer.appendCodePoint(tmp);
                        if (isDot(tmp) && !isNumber) {
                            throw new IllegalStateException(format("invalid number format '%s'", buffer.toString()));
                        } else if (isDot(tmp)) {
                            if (isDecimal) {
                                isNumber = false;
                                break;
                            }
                            isDecimal = true;
                        } else if (!isDigit(tmp)) {
                            isNumber = false;
                            break;
                        } else if (!isNumber) {
                            isNumber = true;
                        }
                    } while (!isWhitespace(tmp = reader.read()) && isNotBracket(tmp) && isNotEof(tmp));

                    if (isNumber) {
                        char firstChar = buffer.charAt(0);
                        if(CharacterClasses.isPositiveSign(firstChar)){
                            buffer.deleteCharAt(0);
                        }
                        tokens.add(number(buffer.toString()));
                        if (isNotEof(tmp)) {
                            reader.unread(tmp);
                        }
                        continue;
                    } else {
                        char[] charArray = buffer.toString().toCharArray();
                        reader.unread(charArray, 1, charArray.length - 1);
                    }
                }

                // looking up for atom
                if (!isWhitespace(ch)) {
                    int tmp = ch;
                    var buffer = new StringBuilder();
                    do {
                        buffer.appendCodePoint(tmp);
                    } while (!isWhitespace((tmp = reader.read())) && isNotBracket(tmp) && isNotEof(tmp));
                    if (isNotEof(tmp)) {
                        reader.unread(tmp);
                    }
                    tokens.add(atom(buffer.toString()));
                    continue;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        tokens.add(eof());
        return tokens;
    }

}
