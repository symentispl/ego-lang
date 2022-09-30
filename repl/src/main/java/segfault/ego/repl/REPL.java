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
package segfault.ego.repl;

import com.github.rvesse.airline.annotations.Command;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import segfault.ego.interpreter.Interpreter;
import segfault.ego.lexer.Lexer;
import segfault.ego.parser.BuiltInScope;
import segfault.ego.parser.GlobalScope;
import segfault.ego.parser.Parser;
import segfault.ego.types.Atom;
import segfault.ego.types.None;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Command(name = "repl")
public class REPL implements Runnable {

    private final GlobalScope globalScope;
    private final Interpreter interpreter;
    private Terminal terminal;
    private LineReader linereader;

    public REPL() {
        Lexer lexer = new Lexer();
        Parser parser = new Parser();
        globalScope = new GlobalScope(new BuiltInScope());
        interpreter = new Interpreter(globalScope, lexer, parser);
        try {
            if (System.console() == null) // a trick to find out if stdin is a terminal
            {
                evalStdinAndMaybeExit();
            }

            terminal = TerminalBuilder.builder().build();
            terminal.writer().println("Welcome to Ego version unknown");
            linereader = LineReaderBuilder.builder()
                    .appName("ego")
                    .terminal(terminal)
                    .build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        var run = true;
        while (run) {
            var signal = read();
            switch (signal) {
                case Read r -> print(eval(r.line()));
                case Exit ignored -> run = false;
            }
        }
    }

    private Signal read() {
        try {
            return new Read(linereader.readLine("> "));
        } catch (EndOfFileException e) {
            return new Exit();
        }
    }

    private void print(Object value) {
        terminal.writer().format(format(value));
    }

    private Object eval(String str) {
        try {
            return interpreter.eval(str);
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    private String format(Object value) {
        if (value instanceof Atom) {
            return String.format("%s\n", globalScope.get(((Atom) value).atom()));
        } else if (value instanceof String) {
            return String.format("\"%s\"\n", value);
        } else if (value != None.none) {
            return String.format("%s\n", value);
        } else {
            return "none";
        }
    }

    private void evalStdinAndMaybeExit() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(CloseShieldInputStream.wrap(System.in)))) {
            reader.lines().forEach(line -> eval(line));
        }
        System.exit(0);
    }
}
