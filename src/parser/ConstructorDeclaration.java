package parser;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

public final class ConstructorDeclaration extends MethodDeclaration {
    public ConstructorDeclaration(TypeDeclaration parent, ExpressionType returnType, Set<Modifier> modifiers, List<VariableDeclaration> formalParameters) throws ParseException {
        super(parent, "new", returnType, modifiers, formalParameters);
        checkArgument(modifiers.contains(Modifier.STATIC));
    }
}
