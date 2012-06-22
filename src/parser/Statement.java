/*
 * Statement.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

public abstract class Statement extends Node {
    // for use in CompilationUnit ONLY
    protected Statement() {
        super();
    }

    protected Statement(Node parent) {
        super(parent);
    }
}
