package net.taken.arcanum.parser.visitors;

import net.taken.arcanum.lang.ArcaInteger;
import net.taken.arcanum.lang.ArcaObject;
import net.taken.arcanum.lang.ArcaString;
import net.taken.arcanum.parser.ArcanumParser;

import static net.taken.arcanum.parser.ArcanumParser.*;

public class ExprVisitor extends ArcanumAbstractVisitor {

    public ExprVisitor(ArcanumVisitor arcanumVisitor) {
        super(arcanumVisitor);
    }

    @Override
    public ArcaInteger visitInt(IntContext ctx) {
        return new ArcaInteger(Integer.valueOf(ctx.getText()));
    }

    @Override
    public ArcaObject visitString(StringContext ctx) {
        return new ArcaString(ctx.getText());
    }

    @Override
    public ArcaObject visitBinaryExpr(BinaryExprContext ctx) {
        // FIXME: for now they only are integer
        int l = ((ArcaInteger)visit(ctx.l)).getValue();
        int r = ((ArcaInteger)visit(ctx.r)).getValue();
        int res;
        switch (ctx.op.getType()) {
            case PLUS:
                res = l + r;
                break;
            case MINUS:
                res = l - r;
                break;
            case MULT:
                res = l * r;
                break;
            case DIV:
                res = l / r;
                break;
            case MOD:
                res = l % r;
                break;
            case POW:
                res = (int) Math.pow(l, r);
                break;
            default: throw new IllegalArgumentException("Unknown operator " + ctx.op);
        }
        return new ArcaInteger(res);
    }

    @Override
    public ArcaObject visitUnaryExpr(UnaryExprContext ctx) {
        int e = ((ArcaInteger)visit(ctx.e)).getValue();
        switch (ctx.op.getType()) {
            case MINUS: return new ArcaInteger(-e);
            default: throw new IllegalArgumentException("Unknown operator " + ctx.op);
        }
    }

    @Override
    public ArcaObject visitAssignment(AssignmentContext ctx) {
        ArcaObject value = visit(ctx.expr());
        environment.putVariable(arcanumVisitor.visitVar(ctx.var()), value);
        return value;
    }

    @Override
    public ArcaObject visitDesignatorExpr(DesignatorExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public ArcaObject visitParenExpr(ParenExprContext ctx) {
        return visit(ctx.expr());
    }

}
