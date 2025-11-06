package poly.compiler.util;

import poly.compiler.parser.tree.Node;
import poly.compiler.parser.tree.expression.QualifiedName;
import poly.compiler.parser.tree.expression.SimpleName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The ClassName class. This class is used to store the full qualified name of a class.
 * This class contains methods that allows to search recursively a class using the
 * class name. It also contains methods to transform the class name into a qualified name string.
 * @author Vincent Philippe (@vincent64)
 */
public final class ClassName {
    /** The Object class' class name. */
    public static final ClassName OBJECT = ClassName.fromStringQualifiedName("java/lang/Object");
    /** The String class' class name. */
    public static final ClassName STRING = ClassName.fromStringQualifiedName("java/lang/String");
    /** The AssertionError class' class name. */
    public static final ClassName ASSERTION_ERROR = ClassName.fromStringQualifiedName("java/lang/AssertionError");
    /** The Throwable class' class name. */
    public static final ClassName THROWABLE = ClassName.fromStringQualifiedName("java/lang/Throwable");
    /** The RuntimeException class' class name. */
    public static final ClassName RUNTIME_EXCEPTION = ClassName.fromStringQualifiedName("java/lang/RuntimeException");
    /** The Enum class' class name. */
    public static final ClassName ENUM = ClassName.fromStringQualifiedName("java/lang/Enum");

    private static final String PACKAGE_SEPARATOR = "/";
    private static final String CLASS_SEPARATOR = "$";
    private static final String DOT = ".";
    private final List<String> packageNames;
    private final List<String> classNames;

    /**
     * Constructs an empty class name.
     */
    public ClassName() {
        //Initialize name lists
        packageNames = new ArrayList<>();
        classNames = new ArrayList<>();
    }

    /**
     * Constructs a class name with the given initial class name.
     * @param className the class name
     */
    public ClassName(String className) {
        this();

        //Add initial class name
        classNames.add(className);
    }

    /**
     * Constructs a class name with the given package name.
     * @param packageName the package name
     */
    public ClassName(PackageName packageName) {
        packageNames = new ArrayList<>(packageName.getNames());
        classNames = new ArrayList<>();
    }

    /**
     * Constructs a class name with the given package and class names arrays.
     * @param packageNames the package names array
     * @param classNames the class names array
     */
    public ClassName(String[] packageNames, String[] classNames) {
        this.packageNames = new ArrayList<>(List.of(packageNames));
        this.classNames = new ArrayList<>(List.of(classNames));
    }

    /**
     * Constructs a class name from the given class name.
     * @param className the class name
     */
    private ClassName(ClassName className) {
        packageNames = new ArrayList<>(className.packageNames);
        classNames = new ArrayList<>(className.classNames);
    }

    /**
     * Constructs a class name with the given package and class names lists.
     * @param packageNames the package names list
     * @param classNames the class names list
     */
    private ClassName(List<String> packageNames, List<String> classNames) {
        this.packageNames = new ArrayList<>(packageNames);
        this.classNames = new ArrayList<>(classNames);
    }

    /**
     * Returns the class name from the given qualified name node.
     * @param node the qualified name node
     * @return the class name from the qualified name
     */
    public static ClassName fromNodeQualifiedName(Node node) {
        ClassName className = new ClassName();

        //Add every qualified name
        while(node instanceof QualifiedName qualifiedName) {
            className.packageNames.addFirst(qualifiedName.getName());
            node = qualifiedName.getQualifiedName();
        }

        //Add first simple name
        SimpleName simpleName = ((SimpleName) node);
        className.packageNames.addFirst(simpleName.getName());

        return className;
    }

    /**
     * Returns the class name from the given qualified name string representation.
     * @param name the qualified name string
     * @return the class name from the qualified name
     */
    public static ClassName fromStringQualifiedName(String name) {
        //Split package and class names
        String[] packageNames = name.split(ClassName.PACKAGE_SEPARATOR);
        String[] classNames = packageNames[packageNames.length - 1].split("\\" + CLASS_SEPARATOR);

        //Build package name without class name
        String[] realPackageNames = new String[packageNames.length - 1];
        System.arraycopy(packageNames, 0, realPackageNames, 0, packageNames.length - 1);

        return new ClassName(realPackageNames, classNames);
    }

    /**
     * Adds the given class name and returns a new class name.
     * @param name the class name
     * @return a new class name
     */
    public ClassName addClassName(String name) {
        ClassName className = new ClassName(this);
        className.classNames.add(name);

        return className;
    }

    /**
     * Returns the first name of the class name.
     * This name can either be from the package or the class name.
     * @return the first name (null if empty)
     */
    public String getFirst() {
        if(!packageNames.isEmpty()) {
            return packageNames.getFirst();
        } else if(!classNames.isEmpty()) {
            return classNames.getFirst();
        } else {
            return null;
        }
    }

    /**
     * Returns the last name of the class name.
     * This name can either be from the package or the class name.
     * @return the last name (null if empty)
     */
    public String getLast() {
        if(!classNames.isEmpty()) {
            return classNames.getLast();
        } else if(!packageNames.isEmpty()) {
            return packageNames.getLast();
        } else {
            return null;
        }
    }

    /**
     * Returns a new class name without the first name.
     * @return a new class name
     */
    public ClassName withoutFirst() {
        ClassName className = new ClassName(this);

        //Remove first element
        if(!className.packageNames.isEmpty()) {
            className.packageNames.removeFirst();
        } else if(!className.classNames.isEmpty()) {
            className.classNames.removeFirst();
        }

        return className;
    }

    /**
     * Returns the qualified name string representation of the class name.
     * @return the qualified name representation
     */
    public String toQualifiedName() {
        StringBuilder string = new StringBuilder();

        //Add package names
        for(int i = 0; i < packageNames.size(); i++) {
            if(i > 0) string.append(DOT);
            string.append(packageNames.get(i));
        }

        //Add class names
        for(String className : classNames) {
            if(!packageNames.isEmpty()) string.append(DOT);
            string.append(className);
        }

        return string.toString();
    }

    /**
     * Returns the internal qualified name string representation of the class name.
     * @return the internal qualified name representation
     */
    public String toInternalQualifiedName() {
        StringBuilder string = new StringBuilder();

        //Add package names
        for(String packageName : packageNames)
            string.append(packageName).append(PACKAGE_SEPARATOR);

        //Add class names
        for(int i = 0; i < classNames.size(); i++) {
            if(i > 0) string.append(CLASS_SEPARATOR);
            string.append(classNames.get(i));
        }

        return string.toString();
    }

    /**
     * Returns the package name.
     * @return the package name
     */
    public PackageName getPackageName() {
        return new PackageName(packageNames);
    }

    /**
     * Returns the outer class name.
     * @return the outer class name
     */
    public ClassName getOuterClassName() {
        if(classNames.size() > 1) {
            List<String> outerClassName = new ArrayList<>(classNames);
            outerClassName.removeLast();

            return new ClassName(packageNames, outerClassName);
        } else {
            return null;
        }
    }

    /**
     * Returns whether the current class name is similar to the given class name.
     * Two class names are similar if they have the same size and the same names.
     * @param that the class name
     * @return true if the class names are equal
     */
    public boolean isSimilarTo(ClassName that) {
        //Make sure they have the same size
        if(this.size() != that.size())
            return false;

        //Compare every name one-by-one
        for(int i = 0; i < this.size(); i++) {
            String thisName = (i >= this.packageNames.size())
                    ? this.classNames.get(i - this.packageNames.size())
                    : this.packageNames.get(i);
            String thatName = (i >= that.packageNames.size())
                    ? that.classNames.get(i - that.packageNames.size())
                    : that.packageNames.get(i);

            if(!thisName.equals(thatName))
                return false;
        }

        return true;
    }

    /**
     * Returns the class name size (i.e. the names count in the qualified name).
     * @return the class name size
     */
    public int size() {
        return packageNames.size() + classNames.size();
    }

    /**
     * Returns whether the class name is empty.
     * @return true if the class name is empty
     */
    public boolean isEmpty() {
        return packageNames.isEmpty() && classNames.isEmpty();
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof ClassName className))
            return false;

        if(packageNames.size() != className.packageNames.size()
                || classNames.size() != className.classNames.size())
            return false;

        for(int i = 0; i < packageNames.size(); i++) {
            if(!packageNames.get(i).equals(className.packageNames.get(i)))
                return false;
        }

        for(int i = 0; i < classNames.size(); i++) {
            if(!classNames.get(i).equals(className.classNames.get(i)))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageNames, classNames);
    }

    /**
     * Returns the qualified name representation of the class name.
     * @return the qualified name representation
     */
    @Override
    public String toString() {
        return toQualifiedName();
    }
}
