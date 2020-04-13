package segfault.ego.parser;

import segfault.ego.lexer.Token;

public record StringLiteralExpr(Token token) implements Expr {
}