/*
 * VirtualMethodInvocation.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class VirtualMethodInvocation extends MethodInvocation {
    private final Expression target;

    public VirtualMethodInvocation(Node parent, MethodSymbol method, Expression target, List<Expression> parameters) {
        super(parent, method, parameters);
        checkNotNull(target);
        checkArgument(!method.isStatic());
        this.target = target;
    }
}
