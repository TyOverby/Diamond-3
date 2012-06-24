/*
 * TypeDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Sets;

import java.util.Set;

import static com.google.common.base.Preconditions.*;

public final class TypeDeclaration extends Statement {
    private final String name;

    private final Set<Modifier> modifiers;

    public TypeDeclaration(CompilationUnit parent, String name, Set<Modifier> modifiers) throws ParseException {
        super(parent);
        checkNotNull(name);
        checkNotNull(modifiers);
        for (Modifier modifier : modifiers) {
            checkArgument(modifier.modifiesTypes());
        }
        this.name = name;
        this.modifiers = Sets.immutableEnumSet(modifiers);
    }
}
