package parser;

public interface ExpressionType {
    boolean isArray();

    boolean isPrimitive();

    boolean isIntegral();
}
