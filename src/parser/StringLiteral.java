package parser;

import static com.google.common.base.Preconditions.*;

public final class StringLiteral extends Expression {
    private final String value;

    public StringLiteral(String value) throws ParseException {
        super(BuiltInType.STRING);
        checkNotNull(value);
        this.value = value;
        for (char c : value.toCharArray()) {
            if (c > 0xff) {
                throw new ParseException("Unicode characters are not supported");
            }
        }
    }
}
