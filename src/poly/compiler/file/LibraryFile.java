package poly.compiler.file;

import poly.compiler.util.ClassName;

import java.io.File;
import java.util.zip.ZipEntry;

/**
 * The LibraryFile abstract class. This class represents an abstract class file in a library.
 * @author Vincent Philippe (@vincent64)
 */
public abstract class LibraryFile {
    private final ClassName className;

    public LibraryFile(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
    }

    /**
     * The LibraryFile.External class. This class represents a class file
     * in an external library (a JAR library).
     */
    public static class External extends LibraryFile {
        private final File file;
        private final ZipEntry entry;

        public External(ClassName className, File file, ZipEntry entry) {
            super(className);
            this.file = file;
            this.entry = entry;
        }

        public File getFile() {
            return file;
        }

        public ZipEntry getEntry() {
            return entry;
        }
    }

    /**
     * The LibraryFile.Java class. This class represents a class file
     * in the Java API library.
     */
    public static class Java extends LibraryFile {
        public Java(ClassName className) {
            super(className);
        }
    }
}
