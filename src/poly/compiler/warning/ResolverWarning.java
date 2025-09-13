package poly.compiler.warning;

import poly.compiler.parser.tree.Node;

/**
 * The ResolverWarning class. Warning classes extending from this class represent
 * code warnings that have been detected during the resolving phase.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class ResolverWarning extends Warning {
    private static final String BASE_MESSAGE = "File %s (on line %s, char. %s) :\n    ";

    public ResolverWarning(Node node, String message) {
        super(BASE_MESSAGE.formatted(
                node.getMeta().getFileName(),
                node.getMeta().getLine(),
                node.getMeta().getCharacter()) + message);
    }

    public static class RedundantPublicInterface extends AnalyzingWarning {
        private static final String MESSAGE = "Redundant 'public' access modifier for interface member";

        public RedundantPublicInterface(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class RedundantPrivateEnum extends AnalyzingWarning {
        private static final String MESSAGE = "Redundant 'private' access modifier for enum constructor";

        public RedundantPrivateEnum(Node node) {
            super(node, MESSAGE);
        }
    }
}
