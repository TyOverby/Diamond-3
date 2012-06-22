/*
 * Expression.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public abstract class Expression extends Node {
    private Node parent;

    private final String type;

    protected Expression(String type) {
        checkNotNull(type);
        parent = null;
        this.type = type;
    }

    @Override
    protected Node getParent() {
        return parent;
    }

    public void attach(Statement parent) {
        checkNotNull(parent);
        checkState(this.parent == null);
        parent.addExpression(this);
        this.parent = parent;
    }

    public String getType() {
        return type;
    }
}
