package poly.compiler.parser.tree;

import poly.compiler.tokenizer.Token;

/**
 * The Node abstract class. This class represents an abstract node in an Abstract Syntax Tree (AST).
 * A node can represent any kind of statement, expression, declaration or other, and may contain child nodes.
 * This class contain the abstract methods used to accept the visit of the node visitor,
 * which is used to traverse the AST when analyzing, optimizing and generating the output code.
 * Extending from this class will also force the subclass to implement the <code>toString</code> method,
 * in order for the AST to be outputted as a string representation using recursive
 * <code>toString</code> calls on child nodes. This class also requires the node to be initialized with
 * metadata information, containing the rough location of the node in the code.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Node {
    /** The node metadata. */
    protected final Meta meta;

    /**
     * Constructs a node with the given metadata information.
     * @param meta the metadata information
     */
    public Node(Meta meta) {
        this.meta = meta;
    }

    /**
     * Returns the node metadata.
     * @return the node metadata
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Accepts the visit of the given node visitor.
     * @param visitor the node visitor
     */
    public abstract void accept(NodeVisitor visitor);

    /**
     * Accepts the visit of the given node modifier and returns the node.
     * @param modifier the node modifier
     * @return the node
     */
    public abstract Node accept(NodeModifier modifier);

    /**
     * Returns the string representation of the node. Implementations of this method
     * may result in recursive calls to child nodes.
     * @return the string representation of the node
     */
    @Override
    public abstract String toString();

    /**
     * Adds the given node element to the given array and returns the new array.
     * This method increase the size of the array to make the addition of the element possible.
     * @param array the node array
     * @param element the node element to add
     * @return the new node array
     */
    public static Node[] add(Node[] array, Node element) {
        //Increase the array size
        Node[] newArray = new Node[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);

        //Add the element
        newArray[array.length] = element;

        return newArray;
    }

    /**
     * Adds the given node element at the beginning of the given array and returns the new array.
     * This method increase the size of the array to make the addition of the element possible.
     * @param array the node array
     * @param element the node element to add
     * @return the new node array
     */
    public static Node[] addFirst(Node[] array, Node element) {
        //Increase the array size
        Node[] newArray = new Node[array.length + 1];
        System.arraycopy(array, 0, newArray, 1, array.length);

        //Add the element at the start
        newArray[0] = element;

        return newArray;
    }

    /**
     * The Node.Meta class. This class contains metadata information about a node,
     * mainly its location in the code file. Since a node can represent a part of code
     * that sprawls accross several lines, the information represents those of the
     * first token making up the node. These information can be useful when debugging.
     */
    public static class Meta {
        private final String fileName;
        private final int line;
        private final int character;

        private Meta(String fileName, int line, int character) {
            this.fileName = fileName;
            this.line = line;
            this.character = character;
        }

        /**
         * Creates and returns the metadata for a node from the leading node token.
         * @param token the leading node token
         * @return the metadata for a node
         */
        public static Meta fromLeadingToken(Token token) {
            return new Meta(token.getMeta().getFileName(),
                    token.getMeta().getLine(),
                    token.getMeta().getCharacter());
        }

        public String getFileName() {
            return fileName;
        }

        public int getLine() {
            return line;
        }

        public int getCharacter() {
            return character;
        }

        @Override
        public String toString() {
            return "Node.Meta(fileName=" + fileName + ", line=" + line + ", character=" + character + ")";
        }
    }
}
