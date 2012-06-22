/*
 * MethodDeclaration.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

public final class MethodDeclaration extends Statement {
    private final MethodSymbol symbol;

    public MethodDeclaration(TypeDeclaration parent, String name, boolean isStatic, TypeSymbol returnType, List<VariableSymbol> formalParameters) throws ParseException {
        super(parent);
        for (VariableSymbol parameter : formalParameters) {
            registerVariableSymbol(parameter);
        }
        List<TypeSymbol> parameters = Lists.transform(formalParameters, new Function<VariableSymbol, TypeSymbol>() {
            @Override
            public TypeSymbol apply(VariableSymbol input) {
                return input.getType();
            }
        });
        symbol = new MethodSymbol(name, isStatic, returnType, parameters);
        getParent().registerMethodSymbol(symbol);
    }

    public MethodSymbol getSymbol() {
        return symbol;
    }
}
