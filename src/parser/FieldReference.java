package parser;

import static com.google.common.base.Preconditions.*;

public final class FieldReference extends VariableReference {
    private final String target;

    public FieldReference(String target, String field) {
        super(field);
        checkNotNull(target);
        this.target = target;
    }
}
