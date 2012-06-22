/*
 * Node.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public abstract class Node {
    protected Node() {
    }

    protected abstract void addChild(Expression child);
}
