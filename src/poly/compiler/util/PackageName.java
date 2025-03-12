package poly.compiler.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The PackageName class. This class is used to store the full package name of a class.
 * @author Vincent Philippe (@vincent64)
 */
public class PackageName {
    private final List<String> names;

    /**
     * Constructs an empty package name.
     */
    public PackageName() {
        //Initialize names array
        names = new ArrayList<>();
    }

    /**
     * Constructs a package name from the given package name.
     * @param packageName the package name
     */
    private PackageName(PackageName packageName) {
        //Initialize names array
        names = new ArrayList<>(packageName.names);
    }

    /**
     * Constructs a package name with the given names list.
     * @param names the names list
     */
    public PackageName(List<String> names) {
        this.names = new ArrayList<>(names);
    }

    /**
     * Constructs a package name with the given package name and name.
     * @param packageName the package name
     * @param name the name
     */
    private PackageName(PackageName packageName, char[] name) {
        this.names = new ArrayList<>(packageName.names);
        names.add(String.valueOf(name));
    }

    /**
     * Adds the given name and returns a new package name.
     * @param name the name
     * @return a new package name
     */
    public PackageName addName(char[] name) {
        return new PackageName(this, name);
    }

    /**
     * Returns the first name.
     * @return the first name
     */
    public String getFirst() {
        return names.getFirst();
    }

    /**
     * Returns a new package name without the first name.
     * @return a new package name
     */
    public PackageName withoutFirst() {
        PackageName packageName = new PackageName(this);
        packageName.names.removeFirst();

        return packageName;
    }

    /**
     * Returns the package names.
     * @return the names
     */
    public List<String> getNames() {
        return new ArrayList<>(names);
    }

    /**
     * Returns whether the package name is empty.
     * @return true if the package name is empty
     */
    public boolean isEmpty() {
        return names.isEmpty();
    }

    @Override
    public String toString() {
        if(names.isEmpty()) return "";

        StringBuilder string = new StringBuilder();
        string.append(names.getFirst());

        for(int i = 1; i < names.size(); i++)
            string.append("/").append(names.get(i));

        return string.toString();
    }
}
