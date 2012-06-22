/*
 * MethodDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

public final class MethodDeclaration extends Statement {
    private final String name;

    private final ExpressionType returnType;

    private final Set<Modifier> modifiers;

    private final List<VariableDeclaration> formalParameters;

    public MethodDeclaration(TypeDeclaration parent, String name, ExpressionType returnType, Set<Modifier> modifiers, List<VariableDeclaration> formalParameters) throws ParseException {
        super(parent);
        checkNotNull(name);
        checkNotNull(returnType);
        checkNotNull(modifiers);
        checkNotNull(formalParameters);
        checkArgument(returnType != BuiltInType.INDETERMINATE);
        this.name = name;
        this.returnType = returnType;
        this.modifiers = ImmutableSet.copyOf(modifiers);
        this.formalParameters = ImmutableList.copyOf(formalParameters);
        for (VariableDeclaration declaration : this.formalParameters) {
            declaration.attach(this);
        }
    }

    public ExpressionType getReturnType() {
        return returnType;
    }
}
