/*
 * Node.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public abstract class Node {
    private final Node parent;

    private final List<Node> children = Lists.newArrayList();

    protected Node() {
        parent = null;
    }

    protected Node(Node parent) {
        checkNotNull(parent);
        this.parent = parent;
        this.parent.children.add(this);
    }

    protected Node getParent() {
        return parent;
    }

    protected void addExpression(Expression child) {
        children.add(child);
    }
}
