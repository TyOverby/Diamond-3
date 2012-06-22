/*
 * Node.java
 * Copyright (C) 2012 Pre-Alpha Software
 * All rights reserved.
 */

package parser;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public abstract class Node {
    private final Node parent;

    private final List<Node> children = Lists.newArrayList();

    private final Map<String, TypeSymbol> typeSymbols = Maps.newHashMap();

    private final Map<String, VariableSymbol> variableSymbols = Maps.newHashMap();

    private final Multimap<String, MethodSymbol> methodSymbols = HashMultimap.create();

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

    protected final void registerTypeSymbol(TypeSymbol symbol) throws ParseException {
        String name = symbol.getName();
        if (typeSymbols.containsKey(name)) {
            throw new ParseException("duplicate type symbol " + name);
        } else {
            typeSymbols.put(name, symbol);
        }
    }

    protected final void registerVariableSymbol(VariableSymbol symbol) throws ParseException {
        String name = symbol.getName();
        if (variableSymbols.containsKey(name)) {
            throw new ParseException("duplicate variable symbol " + name);
        } else {
            variableSymbols.put(name, symbol);
        }
    }

    protected final VariableSymbol resolveVariableSymbol(String name) throws ParseException {
        if (variableSymbols.containsKey(name)) {
            return variableSymbols.get(name);
        } else if (getParent() != null) {
            return getParent().resolveVariableSymbol(name);
        } else {
            throw new ParseException("cannot resolve variable symbol " + name);
        }
    }

    protected final void registerMethodSymbol(MethodSymbol symbol) throws ParseException {
        String name = symbol.getName();
        if (methodSymbols.containsKey(name)) {
            List<TypeSymbol> parameters = symbol.getParameters();
            for (MethodSymbol otherSymbol : methodSymbols.get(name)) {
                if (otherSymbol.getParameters().equals(parameters)) {
                    String message = String.format("duplicate method symbol %s with parameters %s", name, parameters);
                    throw new ParseException(message);
                }
            }
        }
        methodSymbols.put(name, symbol);
    }
}
