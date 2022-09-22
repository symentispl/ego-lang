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

import java.io.Console;
import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import segfault.ego.lexer.Lexer;
import segfault.ego.parser.BuiltInScope;
import segfault.ego.parser.GlobalScope;
import segfault.ego.parser.Parser;
import segfault.ego.types.Atom;
import segfault.ego.types.None;

public class REPL {

    private Lexer lexer;
    private Parser parser;
    private Interpreter interpreter;
    private GlobalContext context;
    private Terminal terminal;
    private LineReader linereader;

    public static void main(String[] args) {
        // var console = System.console();
        // console.printf("Welcome to Ego version unknown\n");
        var repl = new REPL();
        while (true) {
            repl.print(repl.eval(repl.read()));
        }
    }

    REPL() {

        try {
            terminal = TerminalBuilder.builder().build();
            terminal.writer().println("Welcome to Ego version unknown");
            linereader = LineReaderBuilder.builder().terminal(terminal).build();
            lexer = new Lexer();
            var builtInScope = new BuiltInScope();
            var globalScope = new GlobalScope(builtInScope);
            parser = new Parser(globalScope);
            context = GlobalContext.defaulGlobalContext(globalScope);
            interpreter = new Interpreter(context, lexer, parser);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String read() {
        return linereader.readLine("> ");
    }

    private void print(Object value) {
        if (value instanceof Atom) {
            terminal.writer().format("%s\n", context.get(((Atom) value).atom()));
        } else if (value instanceof String) {
            terminal.writer().format("\"%s\"\n", value);
        } else if (value != None.none) {
            terminal.writer().format("%s\n", value);
        }

    }

    private Object eval(String str) {
        try{
            return interpreter.eval(str);
        } catch(Exception e){
            return "error: "+e.getMessage();
        }
    }

}