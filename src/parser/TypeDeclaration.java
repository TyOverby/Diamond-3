/*
 * TypeDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class TypeDeclaration extends Statement {
    private final String name;

    public TypeDeclaration(Statement parent, String name) throws ParseException {
        super(parent);
        checkNotNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
