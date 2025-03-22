package poly.compiler.analyzer;

import poly.compiler.resolver.symbol.ClassSymbol;

/**
 * The Transformer class. This class is used by the Analyzer class to transform
 * some complex parts of the AST. This includes transforming operation overload.
 * @author Vincent Philippe (@vincent64)
 */
public final class Transformer {
    private final Analyzer analyzer;
    private final ClassSymbol classSymbol;

    public Transformer(Analyzer analyzer, ClassSymbol classSymbol) {
        this.analyzer = analyzer;
        this.classSymbol = classSymbol;
    }
}
