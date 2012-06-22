/*
 * ReturnStatement.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(MethodDeclaration method) {
        super(method);
        checkArgument(method.getSymbol().getReturnType() == null);
        returnValue = null;
    }

    public ReturnStatement(MethodDeclaration method, Expression returnValue) {
        super(method);
        checkNotNull(returnValue);
        checkArgument(method.getSymbol().getReturnType().equals(returnValue.getType()));
        this.returnValue = returnValue;
    }
}
