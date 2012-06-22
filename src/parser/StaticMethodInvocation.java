/*
 * StaticMethodInvocation.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class StaticMethodInvocation extends MethodInvocation {
    public StaticMethodInvocation(MethodSymbol method, List<Expression> parameters) {
        super(method, parameters);
        checkArgument(method.isStatic());
    }
}
