package parser;

import java.math.BigInteger;

public final class IntegralLiteral extends Expression {
    private final BigInteger value;

    public IntegralLiteral(BigInteger value) throws ParseException {
        super((value.getLowestSetBit() < 16) ? BuiltInType.SHORT : ((value.getLowestSetBit() < 32) ? BuiltInType.INT : BuiltInType.LONG));
        if (value.getLowestSetBit() >= 64) {
            throw new ParseException("integral literal too long");
        }
        this.value = value;
    }
}
