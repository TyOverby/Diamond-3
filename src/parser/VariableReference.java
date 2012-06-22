/*
 * VariableReference.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class VariableReference extends Expression {
    private final String name;

    public VariableReference(String name) {
        super(BuiltInType.INDETERMINATE);
        checkNotNull(name);
        this.name = name;
    }
}
