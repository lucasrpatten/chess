package ui;

import java.util.List;

@FunctionalInterface
interface GenericFunction<T> {
    T apply();
}

@FunctionalInterface
interface GenericFunctionWithArgs<T, U> {
    T apply(U args);
}

class FunctionPair<T> {
    List<String> keys;
    String help;
    GenericFunction<T> functionNoArgs;
    GenericFunctionWithArgs<T, String> functionWithArgs;

    FunctionPair(List<String> keys, String helpMsg, GenericFunction<T> function) {
        this.keys = keys;
        this.help = helpMsg;
        this.functionNoArgs = function;
    }

    FunctionPair(List<String> keys, String helpMsg, GenericFunctionWithArgs<T, String> function) {
        this.keys = keys;
        this.help = helpMsg;
        this.functionWithArgs = function;
    }

    T apply(String arg) {
        if (functionWithArgs != null) {
            return functionWithArgs.apply(arg);
        }
        else {
            return functionNoArgs.apply();
        }
    }
}