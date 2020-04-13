package segfault.ego.parser;

import static java.util.Arrays.asList;
import static segfault.ego.lexer.Token.atom;

public interface Expr {

    public static ListExpr listExpr(Expr... exprs) {
        return new ListExpr(asList(exprs));
    }

    public static Expr atomExpr(String value) {
        return new AtomExpr(atom(value));
    }

    void accept(Visitor visitor);

}