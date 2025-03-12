package poly.compiler.output.content;

import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Void;
import poly.compiler.analyzer.type.*;
import poly.compiler.error.GeneralError;
import poly.compiler.resolver.Classes;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.FieldSymbol;
import poly.compiler.resolver.symbol.MethodSymbol;
import poly.compiler.util.CharArray;
import poly.compiler.util.Character;
import poly.compiler.util.ClassName;

import java.util.ArrayList;
import java.util.List;

/**
 * The Descriptor class. This class is used to generate the descriptor for field and method symbols,
 * as well as transform descriptors into types.
 * @author Vincent Philippe (@vincent64)
 */
public class Descriptor {
    private static final char PRIMITIVE_BOOLEAN = 'Z';
    private static final char PRIMITIVE_BYTE = 'B';
    private static final char PRIMITIVE_SHORT = 'S';
    private static final char PRIMITIVE_CHAR = 'C';
    private static final char PRIMITIVE_INTEGER = 'I';
    private static final char PRIMITIVE_LONG = 'J';
    private static final char PRIMITIVE_FLOAT = 'F';
    private static final char PRIMITIVE_DOUBLE = 'D';
    private static final char ARRAY = '[';
    private static final char REFERENCE = 'L';
    private static final char VOID = 'V';

    /**
     * Returns the descriptor characters from the given type.
     * @param type the type
     * @return the descriptor
     */
    public static char[] getDescriptorFromType(Type type) {
        CharArray array = new CharArray();

        //Return void descriptor if type is void
        if(type instanceof Void) {
            array.add(VOID);

            return array.getChars();
        }

        //Increase array size for every array type
        while(type instanceof Array arrayType) {
            array.add(ARRAY);

            //Change the current type to the array type
            type = arrayType.getType();
        }

        if(type instanceof Primitive primitiveType) {
            //Set primitive descriptor
            array.add(switch(primitiveType.getPrimitiveKind()) {
                case BOOLEAN -> PRIMITIVE_BOOLEAN;
                case BYTE -> PRIMITIVE_BYTE;
                case CHAR -> PRIMITIVE_CHAR;
                case SHORT -> PRIMITIVE_SHORT;
                case INTEGER -> PRIMITIVE_INTEGER;
                case LONG -> PRIMITIVE_LONG;
                case FLOAT -> PRIMITIVE_FLOAT;
                case DOUBLE -> PRIMITIVE_DOUBLE;
            });
        } else if(type instanceof Object object) {
            //Set class descriptor followed by semicolon
            array.add(REFERENCE);
            array.add(object.getClassSymbol().getClassInternalQualifiedName().toCharArray());
            array.add(';');
        }

        return array.getChars();
    }

    /**
     * Returns the field descriptor characters from the given field symbol.
     * @param fieldSymbol the field symbol
     * @return the field descriptor
     */
    public static char[] generateFieldDescriptor(FieldSymbol fieldSymbol) {
        return getDescriptorFromType(fieldSymbol.getType());
    }

    /**
     * Returns the method descriptor characters from the given method symbol.
     * @param methodSymbol the method symbol
     * @return the method descriptor
     */
    public static char[] generateMethodDescriptor(MethodSymbol methodSymbol) {
        CharArray array = new CharArray();

        //Add opening parameters parenthesis
        array.add('(');

        //Add every parameter
        for(Type parameterType : methodSymbol.getParameterTypes())
            array.add(getDescriptorFromType(parameterType));

        //Add closing parameters parenthesis
        array.add(')');

        //Add method return type
        array.add(getDescriptorFromType(methodSymbol.getReturnType()));

        return array.getChars();
    }

    /**
     * Returns the type from the given descriptor characters.
     * @param descriptor the descriptor
     * @return the type
     */
    private static Type getTypeFromDescriptor(char[] descriptor) {
        //Return void type as null
        if(descriptor[0] == VOID)
            return new Void();

        //Return array type
        if(descriptor[0] == ARRAY)
            return new Array(getTypeFromDescriptor(
                    Character.getSubstring(descriptor, 1, descriptor.length)));

        //Build reference
        if(descriptor[0] == REFERENCE) {
            String reference = String.valueOf(Character.getSubstring(descriptor, 1, descriptor.length - 1));

            ClassSymbol classSymbol = Classes.findClass(ClassName.fromStringQualifiedName(reference));

            if(classSymbol == null)
                new GeneralError.UnresolvableType(reference);

            return new Object(classSymbol);
        }

        return new Primitive(switch(descriptor[0]) {
            case PRIMITIVE_BYTE -> Primitive.Kind.BYTE;
            case PRIMITIVE_CHAR -> Primitive.Kind.CHAR;
            case PRIMITIVE_SHORT -> Primitive.Kind.SHORT;
            case PRIMITIVE_INTEGER -> Primitive.Kind.INTEGER;
            case PRIMITIVE_LONG -> Primitive.Kind.LONG;
            case PRIMITIVE_FLOAT -> Primitive.Kind.FLOAT;
            case PRIMITIVE_DOUBLE -> Primitive.Kind.DOUBLE;
            default -> Primitive.Kind.BOOLEAN;
        });
    }

    /**
     * Returns the field type from the given descriptor characters.
     * @param descriptor the descriptor
     * @return the field type
     */
    public static Type getTypeFromFieldDescriptor(char[] descriptor) {
        return getTypeFromDescriptor(descriptor);
    }

    /**
     * Returns the method types from the given descriptor characters.
     * The last type represents the return type, while all the previous types are the parameter types.
     * @param descriptor the descriptor
     * @return the method types
     */
    public static Type[] getTypesFromMethodDescriptor(char[] descriptor) {
        List<Type> types = new ArrayList<>();

        boolean isReference = false;
        int start = 1;
        int end = 0;
        for(int i = 1; i < descriptor.length; i++) {
            //End parameter types descriptor
            if(descriptor[i] == ')') {
                end = i + 1;
                break;
            }

            //Skip array
            if(descriptor[i] == ARRAY)
                continue;

            //Skip reference
            if(descriptor[i] == REFERENCE && !isReference) {
                isReference = true;
                continue;
            }

            //Build reference
            if(descriptor[i] == ';' && isReference) {
                types.add(getTypeFromDescriptor(Character.getSubstring(descriptor, start, i + 1)));
                start = i + 1;
                isReference = false;
                continue;
            }

            //Build primitive type
            if(!isReference) {
                types.add(getTypeFromDescriptor(Character.getSubstring(descriptor, start, i + 1)));
                start = i + 1;
            }
        }

        //Parse return type descriptor
        types.add(getTypeFromDescriptor(Character.getSubstring(descriptor, end, descriptor.length)));

        return types.toArray(new Type[0]);
    }
}
