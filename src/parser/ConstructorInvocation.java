package parser;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public final class ConstructorInvocation extends MethodInvocation {
    public ConstructorInvocation(Expression target, List<Expression> parameters) {
        super("new", target, parameters);
        for (Expression parameter : parameters) {
            checkArgument(parameter.getType() != BuiltInType.VOID);
        }
    }
}
