/*
 * Expression.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public abstract class Expression extends Node {
    private final TypeSymbol type;

    protected Expression(Node parent, TypeSymbol type) {
        super(parent);
        checkNotNull(type);
        this.type = type;
    }

    public TypeSymbol getType() {
        return type;
    }
}
