package poly.compiler.analyzer;

import poly.compiler.parser.tree.NodeModifier;

/**
 * The Optimizer class. This class is used to optimize the analyzed AST.
 * This includes folding constants and literal values, erasing useless statements and expressions,
 * optimize operations with faster instructions, and much more.
 * @author Vincent Philippe (@vincent64)
 */
public final class Optimizer implements NodeModifier {
    private Optimizer() {
        //TODO
    }

    public static Optimizer getInstance() {
        return new Optimizer();
    }

    public void optimize() {
        //TODO
    }
}
