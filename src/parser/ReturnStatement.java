/*
 * ReturnStatement.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(MethodDeclaration method) throws ParseException {
        super(method);
        checkArgument(method.getReturnType().equals(BuiltInType.VOID));
        returnValue = null;
    }

    public ReturnStatement(MethodDeclaration method, Expression returnValue) throws ParseException {
        super(method);
        checkNotNull(returnValue);
        checkArgument(method.getReturnType().equals(returnValue.getType()));
        this.returnValue = returnValue;
        this.returnValue.attach(this);
    }
}
