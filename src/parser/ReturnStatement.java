/*
 * ReturnStatement.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import static com.google.common.base.Preconditions.*;

public final class ReturnStatement extends Statement {
    private final Expression returnValue;

    public ReturnStatement(Statement parent) throws ParseException {
        super(parent);
        // some ancestor of this statement must be a method declaration, with return type void
        Statement ancestor = parent;
        while (ancestor.getParent() != null) {
            if (ancestor instanceof MethodDeclaration) {
                ExpressionType returnType = ((MethodDeclaration) ancestor).getReturnType();
                checkArgument(returnType == BuiltInType.VOID);
                returnValue = null;
                return;
            }
            ancestor = ancestor.getParent();
        }
        throw new ParseException("\"return\" can only appear within a method");
    }

    public ReturnStatement(Statement parent, Expression returnValue) throws ParseException {
        super(parent);
        // some ancestor of this statement must be a method declaration, with return type consistent with the return value
        Statement ancestor = parent;
        while (ancestor.getParent() != null) {
            if (ancestor instanceof MethodDeclaration) {
                ExpressionType returnType = ((MethodDeclaration) ancestor).getReturnType();
                checkArgument(returnType.equals(returnValue.getType()) || returnValue.getType() == BuiltInType.INDETERMINATE);
                this.returnValue = returnValue;
                this.returnValue.attach(this);
                return;
            }
            ancestor = ancestor.getParent();
        }
        throw new ParseException("\"return\" can only appear within a method");
    }
}
