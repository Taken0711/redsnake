package net.taken.arcanum.parser;

import net.taken.arcanum.domain.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.taken.arcanum.parser.ArcanumParser.*;

public class ArcanumVisitor extends ArcanumParserBaseVisitor<ArcaObject> {

    ArcaKernel kernel;
    Map<ArcaString, ArcaObject> variables;
    Map<ArcaString, Function<ArcaList, ArcaObject>> functions;
    Map<Class<? extends ParseTree>, ArcanumParserBaseVisitor<? extends ArcaObject>> visitors;

    public ArcanumVisitor() {
        kernel = new ArcaKernel();
        variables = new HashMap<>();
        functions = new HashMap<>();
        functions.putAll(kernel.getBuiltInFunctions());
        visitors = new HashMap<>();
        registerVisitor(new ExpressionVisitor(), IntContext.class, BinaryExprContext.class, UnaryExprContext.class,
                AssignmentContext.class, ParenExprContext.class);
    }

    @SafeVarargs
    public final void registerVisitor(ArcanumParserBaseVisitor<? extends ArcaObject> visitor,
                                      Class<? extends ParserRuleContext>... contexts) {
        Arrays.stream(contexts).forEach(ctx -> visitors.put(ctx, visitor));
    }

    @Override
    public ArcaObject visit(ParseTree tree) {
        return tree.accept(visitors.get(tree.getClass()));
    }

    @Override
    public ArcaObject visitVarDesignator(VarDesignatorContext ctx) {
        // TODO handle error
        ArcaString var = visitVar(ctx.var());
        ArcaObject res = variables.get(var);
        if (res == null) {
            res = functions.get(var).apply(new ArcaList());
        }
        return res;
    }

    @Override
    public ArcaObject visitCallWithoutParams(CallWithoutParamsContext ctx) {
        return functions.get(visitVar(ctx.fct)).apply(new ArcaList());
    }

    @Override
    public ArcaObject visitCallWithParams(CallWithParamsContext ctx) {
        return functions.get(visitVar(ctx.fct)).apply(visitParams(ctx.args));
    }

    @Override
    public ArcaString visitVar(VarContext ctx) {
        return new ArcaString(ctx.getText());
    }

    @Override
    public ArcaList visitParams(ParamsContext ctx) {
        return new ArcaList(ctx.expr().stream().map(this::visit).collect(Collectors.toList()));
    }
}
