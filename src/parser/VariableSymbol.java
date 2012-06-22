/*
 * FormalParameter.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.base.Preconditions;import static com.google.common.base.Preconditions.*;

public final class VariableSymbol {
    private final TypeSymbol type;

    private final String name;

    VariableSymbol(TypeSymbol type, String name) {
        Preconditions.checkNotNull(type);
        checkNotNull(name);
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public TypeSymbol getType() {
        return type;
    }
}
