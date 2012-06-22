/*
 * Expression.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class Expression extends Node {
    private Node parent;

    private final List<Expression> children;

    private final String type;

    protected Expression(String type) {
        checkNotNull(type);
        parent = null;
        children = Lists.newArrayList();
        this.type = type;
    }

    @Override
    protected final void addChild(Expression child) {
        checkNotNull(child);
        children.add(child);
    }

    public void attach(Statement parent) {
        checkNotNull(parent);
        checkState(this.parent == null);
        parent.addChild(this);
        this.parent = parent;
    }

    public void attach(Expression parent) {
        checkNotNull(parent);
        checkState(this.parent == null);
        parent.addChild(this);
        this.parent = parent;
    }

    public String getType() {
        return type;
    }
}
