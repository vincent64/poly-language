package poly.compiler.warning;

import poly.compiler.parser.tree.Node;

/**
 * The AnalyzingWarning class. Warning classes extending from this class represent
 * code warnings that have been detected during the analyzing phase.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class AnalyzingWarning extends Warning {
    private static final String BASE_MESSAGE = "File %s (on line %s, char. %s) :\n    ";

    public AnalyzingWarning(Node node, String message) {
        super(BASE_MESSAGE.formatted(
                node.getMeta().getFileName(),
                node.getMeta().getLine(),
                node.getMeta().getCharacter()) + message);
    }

    public static class EmptyBody extends AnalyzingWarning {
        private static final String MESSAGE = "Empty statement body";

        public EmptyBody(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class RedundantCasting extends AnalyzingWarning {
        private static final String MESSAGE = "Redundant casting";

        public RedundantCasting(Node node) {
            super(node, MESSAGE);
        }
    }
}
