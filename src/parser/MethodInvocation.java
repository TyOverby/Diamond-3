/*
 * MethodInvocation.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class MethodInvocation extends Expression {
    private final MethodSymbol method;

    private final List<Expression> parameters;

    protected MethodInvocation(MethodSymbol method, List<Expression> parameters) {
        super(method.getReturnType());
        checkNotNull(method);
        checkArgument(method.getParameters().equals(Lists.transform(parameters, new Function<Expression, TypeSymbol>() {
            @Override
            public TypeSymbol apply(Expression input) {
                return input.getType();
            }
        })));
        this.method = method;
        this.parameters = ImmutableList.copyOf(parameters);
    }
}
