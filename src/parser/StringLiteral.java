package parser;

import static com.google.common.base.Preconditions.*;

public final class StringLiteral extends Expression {
    private final String value;

    public StringLiteral(String rawValue) throws ParseException {
        super(BuiltInType.STRING);
        checkNotNull(rawValue);
        rawValue = rawValue.substring(1, rawValue.length() - 1); // strip the enclosing quotation marks

        StringBuilder escapedValue = new StringBuilder(rawValue.length()); // overestimates somewhat
        char last = 0; // the only thing that matters is that it's not backslash
        for (char c : rawValue.toCharArray()) {
            if (c > 0xff) {
                throw new ParseException("Unicode characters are not supported");
            } else if (last == '\\') {
                // should be an escape sequence
                switch (c) {
                    case 't':
                        escapedValue.append('\t');
                        break;
                    case 'b':
                        escapedValue.append('\b');
                        break;
                    case 'n':
                        escapedValue.append('\n');
                        break;
                    case 'r':
                        escapedValue.append('\r');
                        break;
                    case 'f':
                        escapedValue.append('\f');
                        break;
                    case '\'':
                        escapedValue.append('\'');
                        break;
                    case '\"':
                        escapedValue.append('\"');
                        break;
                    case '\\':
                        escapedValue.append('\\');
                        break;
                    default:
                        throw new ParseException("unknown escape sequence: \\" + c);
                }
            } else if (c != '\\') {
                escapedValue.append(c);
            }

            if (last == '\\' && c == '\\') {
                last = 0;
            } else {
                last = c;
            }
        }

        if (escapedValue.length() > 0xffff) {
            throw new ParseException("string literal too long");
        } else {
            this.value = escapedValue.toString();
        }
    }
}
