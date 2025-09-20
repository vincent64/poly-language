package poly.compiler.error;

import poly.compiler.parser.tree.Node;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.MethodSymbol;
import poly.compiler.util.ClassName;
import poly.compiler.util.MethodStringifier;

/**
 * The ResolvingError class. Error classes extending from this class represent
 * code errors that have been detected during the resolving phase.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class ResolvingError extends Error {
    private static final int CODE = 3;
    private static final String BASE_MESSAGE = "File %s (on line %s, char. %s) :\n    ";

    public ResolvingError(Node node, String message) {
        super(BASE_MESSAGE.formatted(
                node.getMeta().getFileName(),
                node.getMeta().getLine(),
                node.getMeta().getCharacter()) + message,
                CODE);
    }

    public static class DuplicateClass extends ResolvingError {
        private static final String MESSAGE = "Class with name '%s' already exists in current package";

        public DuplicateClass(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class DuplicateField extends ResolvingError {
        private static final String MESSAGE = "Field with name '%s' already exists in current class";

        public DuplicateField(Node node, String fieldName) {
            super(node, MESSAGE.formatted(fieldName));
        }
    }

    public static class DuplicateMethod extends ResolvingError {
        private static final String MESSAGE = "Method with signature '%s' already exists in current class";

        public DuplicateMethod(Node node, MethodSymbol methodSymbol) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol)));
        }
    }

    public static class DuplicateEnumConstant extends ResolvingError {
        private static final String MESSAGE = "Enum constant with name '%s' already exists in current class";

        public DuplicateEnumConstant(Node node, String constantName) {
            super(node, MESSAGE.formatted(constantName));
        }
    }

    public static class InvalidClassAccessModifier extends ResolvingError {
        private static final String MESSAGE = "Class access modifier must be either default or public";

        public InvalidClassAccessModifier(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidConstantInterface extends ResolvingError {
        private static final String MESSAGE = "Interface cannot be constant";

        public InvalidConstantInterface(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidStaticInterface extends ResolvingError {
        private static final String MESSAGE = "Interface cannot be static";

        public InvalidStaticInterface(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidStaticEnum extends ResolvingError {
        private static final String MESSAGE = "Enum cannot be static";

        public InvalidStaticEnum(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidInnerDeclaration extends ResolvingError {
        private static final String MESSAGE = "Inner class can only be declared inside class or inner class";

        public InvalidInnerDeclaration(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedClass extends ResolvingError {
        private static final String MESSAGE = "Class expected";

        public ExpectedClass(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class ExpectedInterface extends ResolvingError {
        private static final String MESSAGE = "Interface expected";

        public ExpectedInterface(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidConstantSuperclass extends ResolvingError {
        private static final String MESSAGE = "Class cannot extends from a constant class";

        public InvalidConstantSuperclass(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidConstantMethod extends ResolvingError {
        private static final String MESSAGE = "Method cannot be constant inside an interface";

        public InvalidConstantMethod(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class CyclicInheritance extends ResolvingError {
        private static final String MESSAGE = "Cyclic inheritance where %s extends itself" +
                "\n    Like LOL, what did you think would happen? Did you think I would let that happen?";

        public CyclicInheritance(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class CyclicImplementation extends ResolvingError {
        private static final String MESSAGE = "Cyclic implementation where %s implements itself" +
                "\n    I know you're desperatly trying to make a diamond. But I will never let you.";

        public CyclicImplementation(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class InvalidInterfaceInheritance extends ResolvingError {
        private static final String MESSAGE = "Interface cannot implement other interfaces";

        public InvalidInterfaceInheritance(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class MissingImplementation extends ResolvingError {
        private static final String MESSAGE = "Class %s should implement method %s";

        public MissingImplementation(Node node, ClassSymbol classSymbol, MethodSymbol methodSymbol) {
            super(node, MESSAGE.formatted(classSymbol.getClassQualifiedName(),
                    MethodStringifier.stringify(methodSymbol)));
        }
    }

    public static class InvalidEmptyMethod extends ResolvingError {
        private static final String MESSAGE = "Method %s can be declared empty only inside an interface";

        public InvalidEmptyMethod(Node node, MethodSymbol methodSymbol) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol)));
        }
    }

    public static class InvalidInterfaceMethod extends ResolvingError {
        private static final String MESSAGE = "Method %s must be public inside an interface";

        public InvalidInterfaceMethod(Node node, MethodSymbol methodSymbol) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol)));
        }
    }

    public static class InvalidEnumConstructor extends ResolvingError {
        private static final String MESSAGE = "Constructor %s must be private inside an enum";

        public InvalidEnumConstructor(Node node, MethodSymbol constructorSymbol) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(constructorSymbol)));
        }
    }

    public static class UnresolvableClass extends ResolvingError {
        private static final String MESSAGE = "Cannot resolve class with name '%s'";

        public UnresolvableClass(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class UnresolvableType extends ResolvingError {
        private static final String MESSAGE = "Cannot resolve type '%s'";

        public UnresolvableType(Node node) {
            super(node, MESSAGE.formatted(node));
        }
    }

    public static class AmbiguousImportation extends ResolvingError {
        private static final String MESSAGE = "Ambiguous class '%s' importations";

        public AmbiguousImportation(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class DuplicateImportation extends ResolvingError {
        private static final String MESSAGE = "Duplicate class '%s' importations";

        public DuplicateImportation(Node node, ClassName className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class UnresolvableImportation extends ResolvingError {
        private static final String MESSAGE = "Cannot resolve importation symbol with name '%s'";

        public UnresolvableImportation(Node node, String className) {
            super(node, MESSAGE.formatted(className));
        }
    }

    public static class InvalidOverrideReturnType extends ResolvingError {
        private static final String MESSAGE = "Cannot override method %s from %s because return type is different";

        public InvalidOverrideReturnType(Node node, MethodSymbol methodSymbol, String className) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol), className));
        }
    }

    public static class InvalidOverrideAccessModifier extends ResolvingError {
        private static final String MESSAGE = "Cannot override method %s from %s because access modifier is weaker";

        public InvalidOverrideAccessModifier(Node node, MethodSymbol methodSymbol, String className) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol), className));
        }
    }

    public static class InvalidConstantOverride extends ResolvingError {
        private static final String MESSAGE = "Cannot override constant method %s from %s";

        public InvalidConstantOverride(Node node, MethodSymbol methodSymbol, String className) {
            super(node, MESSAGE.formatted(MethodStringifier.stringify(methodSymbol), className));
        }
    }

    public static class MissingConstructor extends ResolvingError {
        private static final String MESSAGE = "Missing constructor";

        public MissingConstructor(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidInterfaceConstructor extends ResolvingError {
        private static final String MESSAGE = "Interface cannot contain constructor";

        public InvalidInterfaceConstructor(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidStaticConstructor extends ResolvingError {
        private static final String MESSAGE = "Static class cannot contain constructor";

        public InvalidStaticConstructor(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidStaticSuperclass extends ResolvingError {
        private static final String MESSAGE = "Static class cannot have superclass";

        public InvalidStaticSuperclass(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidEnumSuperclass extends ResolvingError {
        private static final String MESSAGE = "Enum cannot have superclass";

        public InvalidEnumSuperclass(Node node) {
            super(node, MESSAGE);
        }
    }

    public static class InvalidExceptionSuperclass extends ResolvingError {
        private static final String MESSAGE = "Superclass must be a subtype of java.lang.RuntimeException";

        public InvalidExceptionSuperclass(Node node) {
            super(node, MESSAGE);
        }
    }
}
