/*
 * VariableReference.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public class IdentifierReference extends Expression {
    private final String name;

    public IdentifierReference(String name) {
        super(BuiltInType.INDETERMINATE);
        checkNotNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
