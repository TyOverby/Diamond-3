/*
 * MethodDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class MethodDeclaration extends Statement {
    private final String name;

    private final boolean isStatic;

    private final String returnType;

    private final List<VariableDeclaration> formalParameters;

    public MethodDeclaration(TypeDeclaration parent, String name, boolean isStatic, String returnType, List<VariableDeclaration> formalParameters) {
        super(parent);
        checkNotNull(name);
        checkNotNull(returnType);
        checkNotNull(formalParameters);
        this.name = name;
        this.isStatic = isStatic;
        this.returnType = returnType;
        this.formalParameters = Lists.newArrayList(formalParameters);
        for (VariableDeclaration declaration : this.formalParameters) {
            declaration.attach(this);
        }
    }

    public String getReturnType() {
        return returnType;
    }
}
