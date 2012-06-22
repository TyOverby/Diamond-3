/*
 * MethodInvocation.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class MethodInvocation extends Expression {
    private final String method;

    private final Expression target;

    private final List<Expression> parameters;

    public MethodInvocation(String method, Expression target, List<Expression> parameters) {
        super(BuiltInType.INDETERMINATE);
        checkNotNull(method);
        checkNotNull(target);
        checkNotNull(parameters);
        checkArgument(!target.getType().isPrimitive());
        for (Expression parameter : parameters) {
            checkArgument(parameter.getType() != BuiltInType.VOID);
        }
        this.method = method;
        this.target = target;
        this.target.attach(this);
        this.parameters = ImmutableList.copyOf(parameters);
        for (Expression parameter : this.parameters) {
            parameter.attach(this);
        }
    }
}
