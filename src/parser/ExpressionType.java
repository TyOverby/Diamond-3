package parser;

public interface ExpressionType {
    boolean isPrimitive();

    boolean isIntegral();

    boolean isNumeric();

    /**
     * Calculates and returns the number of lexemes required to form the complete type token for this type. This value
     * may also be called the type length. At present, all valid type tokens are represented with an odd number of
     * lexemes; one for a base type, and two more ('[' and ']') for each array dimension.
     *
     * <table>
     *     <tr>
     *         <th>Type token</th>
     *         <th>Type length</th>
     *     </tr>
     *     <tr>
     *         <td>{@code int}</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>{@code UserDefinedType}</td>
     *         <td>1</td>
     *     </tr>
     *     <tr>
     *         <td>{@code UserDefinedType[]}</td>
     *         <td>3</td>
     *     </tr>
     *     <tr>
     *         <td>{@code String[][]}</td>
     *         <td>5</td>
     *     </tr>
     * </table>
     *
     * @return the type length for this type, or the number of lexemes required to form its type token
     */
    int getNumberOfLexemes();
}
