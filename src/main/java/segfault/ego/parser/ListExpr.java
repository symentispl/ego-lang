package segfault.ego.parser;

import java.util.List;

public record ListExpr(List<Expr> exprs) implements Expr {
}