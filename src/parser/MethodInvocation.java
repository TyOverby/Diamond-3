/*
 * MethodInvocation.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class MethodInvocation extends Expression {
    private final String method;

    private final String target;

    private final List<Expression> parameters;

    public MethodInvocation(String method, String target, List<Expression> parameters) {
        super(BuiltInType.INDETERMINATE);
        checkNotNull(method);
        checkNotNull(parameters);
        this.method = method;
        this.target = target;
        this.parameters = ImmutableList.copyOf(parameters);
    }
}
