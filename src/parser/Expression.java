/*
 * Expression.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public abstract class Expression extends Node {
    private Node parent;

    private final TypeSymbol type;

    protected Expression(TypeSymbol type) {
        checkNotNull(type);
        parent = null;
        this.type = type;
    }

    @Override
    protected Node getParent() {
        return parent;
    }

    public void attach(Statement parent) {
        parent.addExpression(this);
        this.parent = parent;
    }

    public TypeSymbol getType() {
        return type;
    }
}
