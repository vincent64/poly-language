package poly.compiler.warning;

import poly.compiler.Parameters;

/**
 * The Warning class. This class is used to display a warning message
 * when an issue has been detected in the code.
 * Unlike the Error class, the Warning class will not halt the compiling process.
 * It will merely print a warning message if the warning flag is enabled, and move on.
 * These warning messages can be seen as "minor" errors, though they
 * should not be dismissed as they provide information to improve the code.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Warning {
    /**
     * Constructs a warning with the given message. The warning flag must be enabled
     * in the parameters for the message to be displayed.
     * @param message the warning message
     */
    public Warning(String message) {
        if(Parameters.warnings()) {
            //Print warning message
            System.out.println("[WARNING] " + message);
        }
    }
}
