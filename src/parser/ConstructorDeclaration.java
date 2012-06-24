package parser;

import com.google.common.collect.ImmutableList;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

// TODO: this should extend MethodDeclaration
public final class ConstructorDeclaration extends Statement {
    private final ExpressionType returnType;

    private final boolean isPrivate;

    private final List<VariableDeclaration> formalParameters;

    public ConstructorDeclaration(TypeDeclaration parent, ExpressionType returnType, Set<Modifier> modifiers, List<VariableDeclaration> formalParameters) throws ParseException {
        super(parent);
        checkNotNull(returnType);
        checkNotNull(modifiers);
        checkNotNull(formalParameters);
        checkArgument(modifiers.equals(EnumSet.of(Modifier.STATIC)) || modifiers.equals(EnumSet.of(Modifier.STATIC, Modifier.PRIVATE)));
        this.returnType = returnType;
        this.isPrivate = modifiers.contains(Modifier.PRIVATE);
        this.formalParameters = ImmutableList.copyOf(formalParameters);
        for (VariableDeclaration declaration : this.formalParameters) {
            declaration.attach(this);
        }
    }
}
