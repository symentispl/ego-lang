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
package segfault.ego.todot;

import static java.lang.String.format;
import static java.lang.System.out;

import com.github.rvesse.airline.annotations.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import segfault.ego.parser.AtomLiteral;
import segfault.ego.parser.Expr;
import segfault.ego.parser.FunCall;
import segfault.ego.parser.FunDecl;
import segfault.ego.parser.ListLiteral;
import segfault.ego.parser.NumberLiteral;
import segfault.ego.parser.ObjectLiteral;
import segfault.ego.parser.ObjectMember;
import segfault.ego.parser.Ref;
import segfault.ego.parser.StringLiteral;
import segfault.ego.parser.ValDecl;

/**
 * This is simple visitor which outputs AST into a dot (graphviz format)
 */
@Command(name = "todot", description = "Outputs Ego AST in DOT format (graphviz)")
public class ToDOT implements Runnable {

    private final Stack<String> nodes = new Stack<>();
    private final Map<String, String> labels = new HashMap<>();
    private int id;

    @Override
    public void run() {
        //        var expr = new Parser( new GlobalScope( new BuiltInScope() ) ).parse( new Lexer().tokenize( "(1 2 3)"
        // ) );
        //        out.println( "graph {" );
        //        expr.accept( this );
        //        out.println( labels.entrySet().stream()
        //                           .map( entry -> Map.entry( entry.getKey(), format( "[label=\"%s\"]",
        // entry.getValue() ) ) )
        //                           .map( entry -> format( "%s %s", entry.getKey(), entry.getValue() ) ).collect(
        // joining( "\n" ) ) );
        //        out.println( "}" );
    }

    public void visit(AtomLiteral atomExpr) {
        var parent = nodes.peek();
        var nodeId = nodeId(atomExpr);
        out.println(format("%s--%s", parent, nodeId));
        labels.put(nodeId, nodeLabel(atomExpr));
    }

    public void visit(ListLiteral listExpr) {
        //        String nodeId = nodeId( listExpr );
        //        if ( !nodes.isEmpty() )
        //        {
        //            var parent = nodes.peek();
        //            out.println( format( "%s--%s", parent, nodeId ) );
        //        }
        //        nodes.push( nodeId );
        //        for ( Expr expr : (List<Expr>) listExpr.exprs() )
        //        {
        //            expr.accept( this );
        //        }
        //        nodes.pop();
        //        labels.put( nodeId, nodeLabel( listExpr ) );
    }

    private String nodeLabel(ListLiteral listExpr) {
        return "list";
    }

    public void visit(StringLiteral stringLiteralExpr) {
        //        var parent = nodes.peek();
        //        String nodeId = nodeId( stringLiteralExpr );
        //        out.println( format( "%s--%s", parent, nodeId ) );
        //        labels.put( nodeId, nodeLabel( stringLiteralExpr ) );
    }

    private String nodeId(Expr expr) {
        return format("%s%d", expr.getClass().getSimpleName(), id++);
    }

    private String nodeLabel(NumberLiteral expr) {
        return format("%s(%s)", expr.getClass().getSimpleName(), expr.token().value());
    }

    private String nodeLabel(AtomLiteral expr) {
        return format("%s(%s)", expr.getClass().getSimpleName(), expr.token().value());
    }

    private String nodeLabel(StringLiteral expr) {
        return format("%s(%s)", expr.getClass().getSimpleName(), expr.token().value());
    }

    public void visit(FunCall funCall) {
        throw new UnsupportedOperationException("function call not implemented");
    }

    public void visit(ValDecl valDecl) {}

    public void visit(ObjectLiteral objectLiteral) {
        throw new UnsupportedOperationException("object literal not supported");
    }

    public void visit(ObjectMember field) {
        throw new UnsupportedOperationException("field not implemented");
    }

    public void visit(FunDecl funDecl) {
        throw new UnsupportedOperationException("function declaration not implemented");
    }

    public void visit(Ref ref) {
        throw new UnsupportedOperationException("ref implementation");
    }

    public void visit(NumberLiteral numberLiteral) {
        var parent = nodes.peek();
        var nodeId = nodeId(numberLiteral);
        out.println(format("%s--%s", parent, nodeId));
        labels.put(nodeId, nodeLabel(numberLiteral));
    }
}
