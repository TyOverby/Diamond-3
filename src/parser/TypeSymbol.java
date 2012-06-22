/*
 * TypeSymbol.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class TypeSymbol {
    private final String name;

    TypeSymbol(String name) {
        checkNotNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
