/*
 * VariableDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class VariableDeclaration extends Expression {
    private final String name;

    public VariableDeclaration(ExpressionType type, String name) {
        super(type);
        checkArgument(type != BuiltInType.VOID);
        checkArgument(type != BuiltInType.INDETERMINATE);
        checkNotNull(name);
        this.name = name;
    }
}
