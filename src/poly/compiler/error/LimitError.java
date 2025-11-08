package poly.compiler.error;

import poly.compiler.output.jvm.Limitations;
import poly.compiler.resolver.ClassDefinition;

/**
 * The LimitError class. Error classes extending from this class represent
 * errors linked to the limitations of the JVM. This can be, for example,
 * exceeding a structure amount and causing overflowing and, therefore,
 * the error would prevent from generating a corrupt class file.
 * An error of this type is exceedingly rare to get, but we are better safe than sorry.
 * Anyone getting a limitation error should probably play the lottery.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class LimitError extends Error {
    private static final int CODE = 9;
    private static final String BASE_MESSAGE = "File %s, class %s :\n    ";

    public LimitError(String fileName, String className, String message) {
        super(BASE_MESSAGE.formatted(fileName, className) + message, CODE);
    }

    public static class ConstantPoolSize extends LimitError {
        private static final String MESSAGE = "Constants amount in constant pool exceeded\n"
                + "Great job on getting this error, it is a rare one. Try to unbrick your class.";

        public ConstantPoolSize(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class FieldCount extends LimitError {
        private static final String MESSAGE = "Fields amount in class exceeded (max. %d)"
                .formatted(Limitations.MAX_FIELDS_COUNT);

        public FieldCount(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class MethodCount extends LimitError {
        private static final String MESSAGE = "Methods amount in class exceeded (max. %d)"
                .formatted(Limitations.MAX_METHODS_COUNT);

        public MethodCount(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class InterfaceCount extends LimitError {
        private static final String MESSAGE = "Interfaces amount in class exceeded (max. %d)"
                .formatted(Limitations.MAX_INTERFACES_COUNT);

        public InterfaceCount(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class IdentifierLength extends LimitError {
        private static final String MESSAGE = "Identifier length in class exceeded (max. %d)"
                .formatted(Limitations.MAX_IDENTIFIER_LENGTH);

        public IdentifierLength(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class MethodParameterCount extends LimitError {
        private static final String MESSAGE = "Method parameters amount in method exceeded (max. %d)"
                .formatted(Limitations.MAX_PARAMETERS_COUNT);

        public MethodParameterCount(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class LocalVariableCount extends LimitError {
        private static final String MESSAGE = "Local variables amount exceeded (max. %d)"
                .formatted(Limitations.MAX_LOCAL_VARIABLES_COUNT);

        public LocalVariableCount(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }

    public static class OperandStackOverflow extends LimitError {
        private static final String MESSAGE = "Operand stack size exceeded (max. %d)"
                .formatted(Limitations.MAX_OPERAND_STACK_SIZE);

        public OperandStackOverflow(ClassDefinition classDefinition) {
            super(classDefinition.getClassDeclaration().getMeta().getFileName(),
                    classDefinition.getClassSymbol().getClassQualifiedName(), MESSAGE);
        }
    }
}
