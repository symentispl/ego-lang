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

class CharacterClasses {

    static final char MINUS = '-';
    static final char PLUS = '+';
    static final char DOT = '.';
    static final char OPENING_BRACKET = '(';
    static final char CLOSING_BRACKET = ')';
    static final char QUOTE = '"';
    static final char COMMENT_START = ';';

    static boolean isQuote(int ch) {
        return ch == QUOTE;
    }

    static boolean isClosingBracket(int ch) {
        return ch == CLOSING_BRACKET;
    }

    static boolean isOpeningBracket(int ch) {
        return ch == OPENING_BRACKET;
    }

    static boolean isCommentStart(int ch) {
        return ch == COMMENT_START;
    }

    static boolean isDot(int tmp) {
        return tmp == DOT;
    }

    static boolean isEof(int ch) {
        return ch == -1;
    }

    static boolean isNotEof(int ch) {
        return ch != -1;
    }

    static boolean isSign(int ch) {
        return ch == PLUS || ch == CharacterClasses.MINUS;
    }

    static boolean isNotBracket(int ch) {
        return ch != OPENING_BRACKET && ch != CLOSING_BRACKET;
    }

	public static boolean isNegativeSign(int ch) {
		return ch == CharacterClasses.MINUS;
	}

	public static boolean isPositiveSign(int ch) {
		return ch == CharacterClasses.PLUS;
	}
}