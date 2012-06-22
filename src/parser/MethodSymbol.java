/*
 * MethodSymbol.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class MethodSymbol {
    private final String name;

    private final boolean isStatic;

    private final TypeSymbol returnType;

    private final List<TypeSymbol> parameters;

    MethodSymbol(String name, boolean isStatic, TypeSymbol returnType, List<TypeSymbol> parameters) {
        checkNotNull(name);
        checkNotNull(parameters);
        this.name = name;
        this.isStatic = isStatic;
        this.returnType = returnType;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    public String getName() {
        return name;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public TypeSymbol getReturnType() {
        return returnType;
    }

    public List<TypeSymbol> getParameters() {
        return parameters;
    }
}
