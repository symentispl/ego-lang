package segfault.ego.parser;

public interface Visitor {

    void visit(ListExpr listExpr);

    void visit(StringLiteralExpr stringLiteralExpr);

    void visit(AtomExpr atomExpr);

}
