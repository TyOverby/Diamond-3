/*
 * VariableReference.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public final class VariableReference extends Expression {
    private final VariableSymbol symbol;

    public VariableReference(Node parent, String name) throws ParseException {
        super(parent, parent.resolveVariableSymbol(name).getType());
        symbol = getParent().resolveVariableSymbol(name);
    }
}
