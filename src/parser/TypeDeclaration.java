/*
 * TypeDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public final class TypeDeclaration extends Statement {
    private final TypeSymbol symbol;

    public TypeDeclaration(Node parent, String name) throws ParseException {
        super(parent);
        symbol = new TypeSymbol(name);
        getParent().registerTypeSymbol(symbol);
    }
}
