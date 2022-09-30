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
package segfault.ego.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static segfault.ego.parser.Expr.stringLiteralExpr;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import segfault.ego.lexer.Lexer;
import segfault.ego.symbols.FunctionSymbol;
import segfault.ego.symbols.ParameterSymbol;
import segfault.ego.symbols.Symbol;
import segfault.ego.symbols.ValSymbol;
import segfault.ego.types.EgoObjectType;
import segfault.ego.types.FunctionType;

public class RewriteASTTest {

    private Lexer lexer;
    private GlobalScope globalScope;
    private Parser parser;

    @BeforeEach
    public void setUp(){
        lexer = new Lexer();
        globalScope = new GlobalScope( new BuiltInScope());
        parser = new Parser( globalScope );
    }

    @Test
    public void string_literal_val_decl() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (val str "str")
                )
                """));
        var symbol = globalScope.resolve("str");
        assertThat(symbol).isInstanceOf(ValSymbol.class).satisfies(valSymbol -> {
            assertThat(valSymbol.name()).isEqualTo("str");
            assertThat(valSymbol.type()).isEqualTo(String.class);
        });
        assertThat( ListLiteral.of( expr ) ).get().extracting( ListLiteral::exprs ).asList().hasSize( 1 ).satisfies( valDecl -> {
            assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                 .extracting( ValDecl::symbol )
                                 .extracting( Symbol::name )
                                 .isEqualTo( "str" );
            assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                 .extracting( ValDecl::expr )
                                 .isEqualTo( stringLiteralExpr( "str" ) );
        }, atIndex( 0 ) );
    }

    @Test
    public void val_reference() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (val str "str")
                    str
                )
                """));
        var symbol = globalScope.resolve("str");
        assertThat(symbol).isInstanceOf(ValSymbol.class).satisfies(valSymbol -> {
            assertThat(valSymbol.name()).isEqualTo("str");
            assertThat(valSymbol.type()).isEqualTo(String.class);
        });
        assertThat( ListLiteral.of( expr ) )
                .get()
                .extracting( ListLiteral::exprs )
                .asList()
                .hasSize( 2 )
                .satisfies( valDecl -> {
                    assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                         .extracting( ValDecl::symbol )
                                         .extracting( Symbol::name )
                                         .isEqualTo( "str" );
                    assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                         .extracting( ValDecl::expr )
                                         .isEqualTo( stringLiteralExpr( "str" ) );
                }, atIndex( 0 ) )
                .satisfies( ref -> {
                    assertThat( ref ).asInstanceOf( type(Ref.class) )
                                     .extracting( Ref::symbol )
                                     .extracting( Symbol::name )
                                     .isEqualTo( "str" );
                } ,atIndex( 1 ) );
    }

    @Test
    public void object_literal_val_decl() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (val obj (object
                                (greeting "Hello")))
                )
                """));
        var symbol = globalScope.resolve("obj");
        assertThat(symbol).isInstanceOf(ValSymbol.class).satisfies(valSymbol -> {
            assertThat(valSymbol.name()).isEqualTo("obj");
            assertThat(valSymbol.type()).isInstanceOf(EgoObjectType.class);
        });
        assertThat( ListLiteral.of( expr ) ).get().extracting( ListLiteral::exprs ).asList().hasSize( 1 ).satisfies( valDecl -> {
            assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                 .extracting( ValDecl::symbol )
                                 .extracting( Symbol::name )
                                 .isEqualTo( "obj" );
            assertThat( valDecl ).asInstanceOf( type( ValDecl.class ) )
                                 .extracting( ValDecl::expr )
                                 .asInstanceOf( type(ObjectLiteral.class) )
                                 .extracting( ObjectLiteral::members )
                                 .asList()
                                 .containsOnly( new ObjectMember( "greeting", stringLiteralExpr( "Hello" ) ) );
        }, atIndex( 0 ) );
    }

    @Test
    public void function_declaration() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (fun greet ((name String)) (returns String)
                                (+ "Hello " name))
                )
                """));
        var symbol = globalScope.resolve("greet");
         assertThat(symbol).isInstanceOf(FunctionSymbol.class).satisfies(funSymbol -> {
             assertThat(funSymbol.name()).isEqualTo("greet");
             assertThat(funSymbol.type()).isEqualTo(new FunctionType( List.of( new ParameterSymbol( "name", String.class )), String.class));
         });
         assertThat( ListLiteral.of( expr ) ).get().extracting( ListLiteral::exprs ).asList().hasSize( 1 ).satisfies( funDecl -> {
             assertThat( funDecl ).asInstanceOf( type( FunDecl.class ) )
                                  .extracting( FunDecl::symbol )
                                  .extracting( Symbol::name )
                                  .isEqualTo( "greet" );
             assertThat( funDecl ).asInstanceOf( type( FunDecl.class ) )
                                  .extracting( FunDecl::symbol )
                                  .extracting( FunctionSymbol::type )
                                  .extracting( FunctionType::parameters )
                                  .asList()
                                  .containsOnly(new ParameterSymbol( "name", String.class));
         }, atIndex( 0 ) );

    }

    @Test
    public void function_call() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (fun greet ((msg String)) (returns String) (+ "Hello " msg))
                    (greet "Jarek")
                )
                """));
        assertThat( ListLiteral.of(expr) ).get().extracting( ListLiteral::exprs ).asList().hasSize(2).satisfies( funCall ->{
            assertThat( funCall ).asInstanceOf( type( FunCall.class ) )
                                 .extracting( FunCall::name )
                                 .isEqualTo( "greet" );
            assertThat( funCall ).asInstanceOf( type( FunCall.class ) )
                                 .extracting( FunCall::parameters )
                                 .asList()
                                 .containsOnly( stringLiteralExpr("Jarek"));
        },atIndex(1));
    }


    @Test
    public void val_lambda() {
        var expr = parser.parse(lexer.tokenize("""
                (
                    (val a (lambda ((name String)) (returns String) (+ "Hello" name)))
                )
                """));
         assertThat( ListLiteral.of(expr) ).get()
                                           .extracting( ListLiteral::exprs )
                                           .asList()
                                           .hasSize(1)
                                           .satisfies( funCall -> assertThat( funCall ).asInstanceOf( type( ValDecl.class ) )
                                                                                                                                              .extracting( ValDecl::expr )
                                                                                                                                              .asInstanceOf( type(Lambda.class) )
                                                                                                                                              .extracting( Lambda::paramsDecl )
                                                                                                                                              .asList()
                                                                                                                                              .containsOnly( new ParameterDecl( "name", String.class )), atIndex( 0));
    }
}