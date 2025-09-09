package poly.compiler.warning;

/**
 * The GeneralWarning class. Warning classes extending from this class represent
 * code warnings linked to the compilation process in general.
 * @author Vincent Philippe (@vincent64)
 */
public class GeneralWarning extends Warning {
    public GeneralWarning(String message) {
        super(message);
    }

    public static class ClassCollision extends GeneralWarning {
        private static final String MESSAGE = "Colliding library classes with the name '%s'";

        public ClassCollision(String className) {
            super(MESSAGE.formatted(className));
        }
    }
}
