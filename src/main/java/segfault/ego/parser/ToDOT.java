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
package segfault.ego.parser;

import segfault.ego.lexer.Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.stream.Collectors.joining;

/**
 * This is simple visitor which outputs AST into a dot (graphviz format)
 */
public class ToDOT implements Visitor {

    private final Stack<String> nodes = new Stack<>();
    private final Map<String,String> labels = new HashMap<>();
    private int id;

    public static void main( String[] args ) throws IOException {

        if ( args.length < 1 ) {
            throw new IllegalArgumentException( "expected one argument, an ego file" );
        }

        Path egoFile = Paths.get( args[0] );

        if ( !Files.isReadable( egoFile ) ) {
            throw new IllegalArgumentException( format( "ego file %s is not accessible", egoFile ) );
        }

        var expr = new Parser().parse( new Lexer().tokenize( Files.readString( egoFile ) ) );

        ToDOT visitor = new ToDOT();
        out.println( "graph {" );
        expr.accept( visitor );
        out.println( visitor.labels.entrySet().stream()
                                   .map( entry -> Map.entry( entry.getKey(), format( "[label=\"%s\"]", entry.getValue() ) ) )
                                   .map( entry -> format( "%s %s", entry.getKey(), entry.getValue() ) ).collect( joining( "\n" ) ) );
        out.println( "}" );
    }

    @Override
    public void visit( AtomExpr atomExpr ) {
        var parent = nodes.peek();
        var nodeId = nodeId( atomExpr );
        out.println( format( "%s--%s", parent, nodeId ) );
        labels.put( nodeId, nodeLabel( atomExpr ) );
    }

    @Override
    public void visit( ListExpr listExpr ) {
        String nodeId = nodeId( listExpr );
        if ( !nodes.isEmpty() ) {
            var parent = nodes.peek();
            out.println( format( "%s--%s", parent, nodeId ) );
        }
        nodes.push( nodeId );
        for ( Expr expr : (List<Expr>) listExpr.exprs() ) {
            expr.accept( this );
        }
        nodes.pop();
        labels.put( nodeId, nodeLabel( listExpr ) );
    }

    @Override
    public void visit( StringLiteralExpr stringLiteralExpr ) {
        var parent = nodes.peek();
        String nodeId = nodeId( stringLiteralExpr );
        out.println( format( "%s--%s", parent, nodeId ) );
        labels.put( nodeId, nodeLabel( stringLiteralExpr ) );
    }

    @Override
    public void visit( NumberLiteralExpr numberLiteralExpr) {
        var parent = nodes.peek();
        String nodeId = nodeId( numberLiteralExpr );
        out.println( format( "%s--%s", parent, nodeId ) );
        labels.put( nodeId, nodeLabel( numberLiteralExpr ) );
    }

    private String nodeId( Expr expr ) {
        return format( "%s%d", expr.getClass().getSimpleName(), id++ );
    }

    private String nodeLabel( ListExpr expr ) {
        return expr.getClass().getSimpleName();
    }

    private String nodeLabel( AtomExpr expr ) {
        return format( "%s(%s)", expr.getClass().getSimpleName(), expr.token().value() );
    }

    private String nodeLabel( StringLiteralExpr expr ) {
        return format( "%s(%s)", expr.getClass().getSimpleName(), expr.token().value() );
    }

    private String nodeLabel( NumberLiteralExpr expr ) {
        return format( "%s(%s)", expr.getClass().getSimpleName(), expr.token().value() );
    }
}