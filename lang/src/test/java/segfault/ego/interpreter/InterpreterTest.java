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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import segfault.ego.lexer.Lexer;
import segfault.ego.parser.BuiltInScope;
import segfault.ego.parser.GlobalScope;
import segfault.ego.parser.Parser;
import segfault.ego.types.None;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest
{

    @Test
    @Disabled
    public void eval_val_print() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                               ((val a "Hello")
                                               (print a))
                                               """ );
        assertThat( result ).isEqualTo( None.none );
    }

    @Test
    public void eval_val() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                               ((val a "Hello")
                                                a)
                                               """ );
        assertThat( result ).asList().containsOnly( "Hello", "Hello" );
    }

    @Test
    public void eval_string_concat() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                               (+ "Hello " "Wiktor"))
                                               """ );
        assertThat( result ).isEqualTo( "Hello Wiktor" );
    }


    @Test
    public void eval_lambda() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                               (val greet2 (lambda ((name String)) (returns String) (+ "Hello " name) ))
                                               """ );
        result = interpreter.eval("""
                       (greet2 "Jarek")""");
        assertThat( result ).isEqualTo( "Hello Jarek" );
    }

    @Test
    public void fun_decl() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                               (fun greet2  ((name String)) (returns String) (+ "Hello " name) )
                                               """ );
        result = interpreter.eval("""
                       (greet2 "Jarek")""");
        assertThat( result ).isEqualTo( "Hello Jarek" );
    }

    @Test
    @Disabled
    public void inline_lambda() {
        var builtInScope = new BuiltInScope();
        var globalScope = new GlobalScope( builtInScope );
        var interpreter = new Interpreter( GlobalContext.defaulGlobalContext( globalScope ), new Lexer(), new Parser( globalScope ) );
        var result = interpreter.eval( """
                                            (
                                                (lambda 
                                                    ((name String)) (returns String)
                                                    (+ "Hello " name)
                                                )

                                                "Jarek"
                                            )
                                               """ );
        System.out.println(result);
        assertThat( result ).isEqualTo( "Hello Jarek" );
    }

}