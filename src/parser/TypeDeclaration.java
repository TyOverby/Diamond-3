/*
 * TypeDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

public final class TypeDeclaration extends Statement {
    private final String name;

    private final Set<Modifier> modifiers;

    public TypeDeclaration(Statement parent, String name, Set<Modifier> modifiers) throws ParseException {
        super(parent);
        checkNotNull(name);
        checkNotNull(modifiers);
        checkArgument(modifiers.isEmpty() || modifiers.equals(EnumSet.of(Modifier.PRIVATE)));
        if (parent instanceof CompilationUnit) {
            checkArgument(modifiers.isEmpty());
        }
        this.name = name;
        this.modifiers = Sets.immutableEnumSet(modifiers);
    }

    public String getName() {
        return name;
    }
}
