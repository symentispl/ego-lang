package segfault.ego.parser;

import static segfault.ego.parser.Expr.atomExpr;
import static segfault.ego.parser.Expr.listExpr;
import static segfault.ego.parser.Expr.stringLiteralExpr;

import java.util.ArrayList;
import java.util.List;

import segfault.ego.lexer.Kind;
import segfault.ego.lexer.Lexer;
import segfault.ego.lexer.Token;

public class Parser {

    public static void main(String[] args) {

        var lexer = new Lexer();
        var parser = new Parser();

        var expr = parser.parse(lexer.tokenize("()"));
        assert listExpr().equals(expr);

        expr = parser.parse(lexer.tokenize("(())"));
        assert listExpr(
                    listExpr())
                .equals(expr);

        expr = parser.parse(lexer.tokenize("((atom))"));
        assert listExpr(
                    listExpr(
                        atomExpr("atom")))
                .equals(expr);

        expr = parser.parse(lexer.tokenize("((atom))"));
        assert listExpr(
                    listExpr(
                        atomExpr("atom")))
                .equals(expr);
    
        expr = parser.parse(lexer.tokenize("((atom)())"));
        assert listExpr(
                    listExpr(
                        atomExpr("atom")),
                    listExpr())
                .equals(expr);

        expr = parser.parse(lexer.tokenize("""
        ((atom (help "Me")))"""));
        assert listExpr(
                        listExpr( 
                            atomExpr("atom"),
                            listExpr( 
                                atomExpr("help"),stringLiteralExpr("Me"))))
                        .equals(expr);
        
    }

    public Expr parse(List<Token> tokens) {
        var iterator = new PushbackIterator<>(tokens.iterator());
        return exprRule(iterator);
    }

    private Expr listExprRule(PushbackIterator<Token> tokens) {
        var elements = new ArrayList<Expr>();

        while (tokens.hasNext()) {
            var token = tokens.next();
            if (token.kind() == Kind.CLOSING_BRACKET) {
                return new ListExpr(elements);
            }
            tokens.pushback(token);
            elements.add(exprRule(tokens));
        }
        throw new IllegalStateException();
    }

    private Expr exprRule(PushbackIterator<Token> tokens) {
        while (tokens.hasNext()) {
            var token = tokens.next();
            if (token.kind() == Kind.OPENING_BRACKET) {
                return listExprRule(tokens);
            }

            if (token.kind() == Kind.STRING) {
                return new StringLiteralExpr(token);
            }

            if (token.kind() == Kind.ATOM) {
                return new AtomExpr(token);
            }

            if (token.kind() == Kind.EOF) {
                // no op;
            }
        }
        throw new IllegalStateException();
    }
}