package segfault.ego.parser;

import segfault.ego.lexer.Token;

public record AtomExpr(Token token) implements Expr {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
