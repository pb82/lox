package com.craftinginterpreters.lox;

import java.util.List;

public class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment enclosing;
    private final boolean isInitializer;

    LoxFunction(Stmt.Function declaration, Environment enclosing, boolean isInitializer) {
        this.enclosing = enclosing;
        this.declaration = declaration;
        this.isInitializer = isInitializer;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(enclosing);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return enclosing.getAt(0, "this");
            return returnValue.value;
        }

        if (isInitializer) return enclosing.getAt(0, "this");

        return null;
    }

    public LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(enclosing);
        environment.define("this", instance);
        return new LoxFunction(declaration, environment, isInitializer);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "LoxFunction{" +
                "name=" + declaration.name.lexeme +
                '}';
    }
}
