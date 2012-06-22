/*
 * Statement.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class Statement extends Node {
    private final Statement parent;

    private final List<Node> children = Lists.newArrayList();

    // for use in CompilationUnit ONLY
    protected Statement() {
        parent = null;
    }

    protected Statement(Statement parent) throws ParseException {
        checkNotNull(parent);
        parent.addChild(this);
        this.parent = parent;
    }

    public final Statement getParent() {
        return parent;
    }

    protected void addChild(Statement child) throws ParseException {
        checkNotNull(child);
        children.add(child);
    }

    @Override
    protected final void addChild(Expression child) {
        checkNotNull(child);
        children.add(child);
    }
}
