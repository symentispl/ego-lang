package segfault.ego.parser;

import segfault.ego.lexer.Token;

public record StringLiteralExpr(Token token) implements Expr{
    @Override
    public void accept( Visitor visitor )
    {
        visitor.visit( this );
    }
}