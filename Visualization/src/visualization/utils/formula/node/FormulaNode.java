package visualization.utils.formula.node;

public interface FormulaNode {
    /**
     * A node in a formula must have an identifier, so we have to be able to get it somehow.
     *
     * @return a String containing the id
     */
    String getId();

    /**
     * A node in a formula must have a name, so we have to get it somehow.
     *
     * @return a String containing the names
     */
    String getName();
}
