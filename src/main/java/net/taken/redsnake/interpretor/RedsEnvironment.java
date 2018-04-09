package net.taken.redsnake.interpretor;

import net.taken.redsnake.lang.RedsInteger;
import net.taken.redsnake.lang.RedsObject;
import net.taken.redsnake.operations.BinaryOperation;
import net.taken.redsnake.operations.OperationTable;
import net.taken.redsnake.operations.OperatorType;
import net.taken.redsnake.operations.UnaryOperation;
import net.taken.redsnake.reflect.Type;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static net.taken.redsnake.operations.OperatorType.*;

public class RedsEnvironment {

    RedsKernel kernel;
    PrintWriter stdout;
    Map<String, VariableSymbol> variables;
    Map<String, Function<List<Value>, Value>> functions;
    OperationTable operationTable;

    public RedsEnvironment() {
        this(new OutputStreamWriter(System.out));
    }

    public RedsEnvironment(Writer stdout) {
        kernel = new RedsKernel(this);
        this.stdout = new PrintWriter(stdout);
        variables = new HashMap<>();
        functions = new HashMap<>();
        functions.putAll(kernel.getBuiltInFunctions());
        initOperations();
    }

    private void initOperations() {
        operationTable = new OperationTable();
        // TODO equality problem, super type ?
        // Integers
        operationTable.registerBinaryOperation(PLUS, new BinaryOperation<>(RedsInteger.TYPE, RedsInteger.TYPE, RedsInteger.TYPE, (x, y) -> new RedsInteger(x.getValue() + y.getValue())));
        operationTable.registerBinaryOperation(MINUS, new BinaryOperation<>(RedsInteger.TYPE, RedsInteger.TYPE, RedsInteger.TYPE, (x, y) -> new RedsInteger(x.getValue() - y.getValue())));
    }

    public <T extends RedsObject> void registerUnaryOperation(OperatorType operatorType, Type<T> type, UnaryOperation<T, RedsObject> function) {
        operationTable.registerUnaryOperation(operatorType, type, function);
    }

    public <T extends RedsObject> Optional<UnaryOperation> resolveUnaryOperation(OperatorType operatorType, Type<T> type) {
        return operationTable.resolveUnaryOperation(operatorType, type);
    }

    public <T extends RedsObject, U extends RedsObject> void registerBinaryOperation(OperatorType operatorType, BinaryOperation<T, U, RedsObject> function) {
        operationTable.registerBinaryOperation(operatorType, function);
    }

    public <T extends RedsObject, U extends RedsObject> Optional<BinaryOperation<T, U, ? extends RedsObject>> resolveBinaryOperation(OperatorType operatorType, Type<T> type1, Type<U> type2) {
        return operationTable.resolveBinaryOperation(operatorType, type1, type2);
    }

    public VariableSymbol getVariable(String name) {
        return variables.get(name);
    }

    public void putVariable(VariableSymbol value) {
        variables.put(value.getName(), value);
    }

    public Function<List<Value>, Value> resolveFunction(String name) {
        return functions.get(name);
    }

    public void putFunction(String name, Function<List<Value>, Value> function) {
        functions.put(name, function);
    }
}
