package poly.compiler.analyzer.content;

/**
 * The SpecialMethod class.
 */
public class SpecialMethod {
    /**
     * The SpecialMethod.Name class. This class contains the internal name
     * of every special methods.
     */
    public static class Name {
        //Constructor names
        public static final String CONSTRUCTOR = "<init>";
        public static final String STATIC_CONSTRUCTOR = "<clinit>";

        //Main method name
        public static final String MAIN = "main";
    }
}
