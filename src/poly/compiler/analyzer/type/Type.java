package poly.compiler.analyzer.type;

import poly.compiler.analyzer.table.ImportTable;
import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.ArrayType;
import poly.compiler.parser.tree.expression.PrimitiveType;
import poly.compiler.parser.tree.expression.QualifiedName;
import poly.compiler.parser.tree.expression.SimpleName;
import poly.compiler.resolver.Classes;
import poly.compiler.resolver.LibraryClasses;
import poly.compiler.resolver.symbol.ClassSymbol;
import poly.compiler.resolver.symbol.Symbol;
import poly.compiler.util.ClassName;

/**
 * The Type abstract class. This class represents a type, which can be either
 * a primitive, an object or an array.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class Type {
    private final Kind kind;

    /**
     * Constructs a type with the given kind.
     * @param kind the type kind
     */
    public Type(Kind kind) {
        this.kind = kind;
    }

    /**
     * Returns the type kind.
     * @return the type kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns whether the current type can be assigned to the given type.
     * A type B is assignable to a type A either if A and B are the same type,
     * or if B is a subtype of A if A and B are objects.
     * @param that the other type
     * @return true if the type is assignable to the current type
     */
    public boolean isAssignableTo(Type that) {
        if(this.equals(that))
            return true;

        if(this instanceof Object thisObject && that instanceof Object thatObject)
            return thisObject.getClassSymbol().isSubtypeOf(thatObject.getClassSymbol());

        return false;
    }

    /**
     * Returns the type from the given type node.
     * @param node the type node
     * @param classSymbol the current class symbol
     * @param importTable the importations table
     * @return the type
     */
    public static Type fromTypeNode(Node node, ClassSymbol classSymbol, ImportTable importTable) {
        //Get primitive type
        if(node instanceof PrimitiveType primitiveType)
            return new Primitive(primitiveType.getKind());

        //Get array type
        if(node instanceof ArrayType arrayType) {
            Type type = fromTypeNode(arrayType.getType(), classSymbol, importTable);

            if(type != null) return new Array(type);
            else return null;
        }

        //Get object type from simple name
        if(node instanceof SimpleName simpleName) {
            //Get class name from simple name
            String className = simpleName.getName();

            ClassSymbol classTypeSymbol;

            //Find class as self class
            if(classSymbol.getName().equals(className))
                return new Object(classSymbol);

            //Find class in importations table
            if((classTypeSymbol = importTable.findImportation(className)) != null)
                return new Object(classTypeSymbol);

            //Find class in current class
            if((classTypeSymbol = classSymbol.findClass(className)) != null)
                return new Object(classTypeSymbol);

            //Find class in outer classes
            Symbol ownerSymbol = classSymbol.getOwnerSymbol();
            while(ownerSymbol instanceof ClassSymbol ownerClassSymbol) {
                if((classTypeSymbol = classSymbol.findClass(className)) != null)
                    return new Object(classTypeSymbol);

                ownerSymbol = ownerClassSymbol.getOwnerSymbol();
            }

            //Find class in current package
            if((classTypeSymbol = classSymbol.getPackageSymbol().findClass(className)) != null)
                return new Object(classTypeSymbol);

            //Find object class
            if(className.equals(ClassName.OBJECT.getLast()))
                return new Object(LibraryClasses.findClass(ClassName.OBJECT));

            //Find string class
            if(className.equals(ClassName.STRING.getLast()))
                return new Object(LibraryClasses.findClass(ClassName.STRING));
        }

        //Get object type from qualified name
        if(node instanceof QualifiedName qualifiedName) {
            //Get class name from qualified name
            ClassName className = ClassName.fromNodeQualifiedName(qualifiedName);

            ClassSymbol classTypeSymbol;

            //Find class in importations table
            if((classTypeSymbol = importTable.findImportation(className.getFirst())) != null) {
                if((classTypeSymbol = classTypeSymbol.findClass(className.withoutFirst())) != null)
                    return new Object(classTypeSymbol);
            }

            //Find class in current package
            if((classTypeSymbol = classSymbol.getPackageSymbol().findClass(className)) != null)
                return new Object(classTypeSymbol);

            //Find class in project root
            if((classTypeSymbol = Classes.findClass(className)) != null)
                return new Object(classTypeSymbol);
        }

        return null;
    }

    /**
     * Returns whether the given type is equal to the current type.
     * @param object the type
     * @return true if the types are equal
     */
    @Override
    public abstract boolean equals(java.lang.Object object);

    /**
     * Returns the string representation of the type.
     * This method returns an appropriate representation for error messages.
     * @return the string representation of the type
     */
    @Override
    public abstract String toString();

    /**
     * The Type.Kind enum. This enum contains the three different type
     * there exists.
     */
    public enum Kind {
        PRIMITIVE,
        ARRAY,
        OBJECT
    }
}
