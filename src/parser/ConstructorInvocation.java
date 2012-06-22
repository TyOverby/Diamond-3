package parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class ConstructorInvocation extends Expression {
    private final String target;

    private final List<Expression> parameters;

    public ConstructorInvocation(String target, List<Expression> parameters) {
        super(BuiltInType.INDETERMINATE);
        checkNotNull(parameters);
        for (Expression parameter : parameters) {
            checkArgument(parameter.getType() != BuiltInType.VOID);
        }
        this.target = target;
        this.parameters = ImmutableList.copyOf(parameters);
        for (Expression parameter : this.parameters) {
            parameter.attach(this);
        }
    }
}
