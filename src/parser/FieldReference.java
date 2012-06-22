package parser;

import static com.google.common.base.Preconditions.*;

public final class FieldReference extends IdentifierReference {
    private final Expression target;

    public FieldReference(Expression target, String field) {
        super(field);
        checkNotNull(target);
        checkArgument(!target.getType().isPrimitive());
        this.target = target;
        this.target.attach(this);
    }
}
