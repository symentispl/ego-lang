package segfault.ego.parser;

import java.util.List;

public record ListExpr(List<Expr> exprs) implements Expr {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}