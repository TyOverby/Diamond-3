/*
 * VariableDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public final class VariableDeclaration extends Statement {
    private final VariableSymbol symbol;

    public VariableDeclaration(Node parent, TypeSymbol type, String name) throws ParseException {
        super(parent);
        symbol = new VariableSymbol(type, name);
        getParent().registerVariableSymbol(symbol);
    }
}
