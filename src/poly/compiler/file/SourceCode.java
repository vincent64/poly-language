package poly.compiler.file;

import poly.compiler.util.PackageName;

/**
 * The SourceCode class. This class represents a single source code file written in Poly,
 * and contains its content, its name and package name.
 * @author Vincent Philippe (@vincent64)
 */
public class SourceCode {
    private final String fileName;
    private final PackageName packageName;
    private final char[] content;

    /**
     * Constructs a source code with the given file name, package name and content.
     * @param fileName the file name
     * @param packageName the package name
     * @param content the code content
     */
    public SourceCode(String fileName, PackageName packageName, char[] content) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.content = content;
    }

    /**
     * Returns the source file name.
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the source package name.
     * @return the package name
     */
    public PackageName getPackageName() {
        return packageName;
    }

    /**
     * Returns the source file name, including the package name and file extension.
     * @return the full file name
     */
    public String getFullFileName() {
        return packageName + (packageName.isEmpty() ? "" : "/") + fileName + CodeReader.EXTENSION;
    }

    /**
     * Returns the source code content.
     * @return the content
     */
    public char[] getContent() {
        return content;
    }
}
