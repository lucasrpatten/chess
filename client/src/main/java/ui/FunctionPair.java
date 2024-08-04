package ui;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface GenericFunction<T> {
    T apply();
}

@FunctionalInterface
interface GenericFunctionWithArgs<T, U> {
    T apply(U args);
}

class Arguments {
    private List<String> args;

    Arguments(String arg) {
        this.args = List.of(arg);
    }

    Arguments(List<String> args) {
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        // List<String> formattedArgs = new ArrayList<>();
        String ss = "";

        for (String arg : args) {
            String formatted = arg.contains("|") ? String.format("[%s] ", arg) : String.format("<%s> ", arg);
            ss += formatted;
        }
        return ss;
    }
}

class FunctionPair<T> {
    private List<String> keys;
    private String help;
    private Arguments args;
    private GenericFunction<T> functionNoArgs;
    private GenericFunctionWithArgs<T, String> functionWithArgs;

    FunctionPair(List<String> keys, String helpMsg, GenericFunction<T> function) {
        this.keys = keys;
        this.help = helpMsg;
        this.functionNoArgs = function;
    }

    FunctionPair(List<String> keys, Arguments args, String helpMsg, GenericFunctionWithArgs<T, String> function) {
        this.keys = keys;
        this.args = args;
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

    public List<String> getKeys() {
        return keys;
    }

    public String getHelp() {
        return help;
    }

    public Arguments getArgs() {
        return args;
    }

    public GenericFunction<T> getFunctionNoArgs() {
        return functionNoArgs;
    }

    public GenericFunctionWithArgs<T, String> getFunctionWithArgs() {
        return functionWithArgs;
    }
}