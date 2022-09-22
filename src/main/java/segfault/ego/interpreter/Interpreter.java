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
package segfault.ego.interpreter;

import segfault.ego.lexer.Lexer;
import segfault.ego.parser.Parser;

public class Interpreter {

    private final Lexer lexer;
    private final Parser parser;
    private final GlobalContext context;

    public Interpreter(GlobalContext context, Lexer lexer, Parser parser) {
        this.context = context;
        this.lexer = lexer;
        this.parser = parser;
    }

    public Object eval(String str) {
        var expr = parser.parse(lexer.tokenize(str));
        var interpreterVisitor = new InterpreterVisitor(context);
        expr.accept(interpreterVisitor);
        return interpreterVisitor.returns();
    }

}
