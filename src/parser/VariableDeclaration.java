/*
 * VariableDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import java.util.EnumSet;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

public final class VariableDeclaration extends Expression {
    private final String name;

    private final boolean isPrivate;

    public VariableDeclaration(ExpressionType type, String name, Set<Modifier> modifiers) {
        super(type);
        checkNotNull(name);
        checkNotNull(modifiers);
        checkArgument(type != BuiltInType.VOID);
        checkArgument(type != BuiltInType.INDETERMINATE);
        checkArgument(modifiers.isEmpty() || modifiers.equals(EnumSet.of(Modifier.PRIVATE)));
        this.name = name;
        isPrivate = !modifiers.isEmpty();
    }
}
