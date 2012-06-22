/*
 * VariableDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public final class VariableDeclaration extends Expression {
    private final VariableSymbol symbol;

    public VariableDeclaration(TypeSymbol type, String name) throws ParseException {
        super(type);
        symbol = new VariableSymbol(type, name);
        getParent().registerVariableSymbol(symbol);
    }
}
