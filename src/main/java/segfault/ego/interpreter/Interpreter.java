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
    public static void main(String[] args) {
        new Interpreter().eval(args[0]);
    }

    public Object eval(String str) {
        var expr = new Parser().parse(new Lexer().tokenize(str));

        var interpreterVisitor = new InterpreterVisitor();

        expr.accept(interpreterVisitor);
        return interpreterVisitor.returns();
    }

}