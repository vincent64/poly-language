package poly.compiler.output;

/**
 * The Byteable interface. This simple interface describes a class whose content
 * can be compiled into an array of bytes. It is used by every class with some
 * content that can be compiled and appended to a class file.
 * @author Vincent Philippe (@vincent64)
 */
public interface Byteable {
    byte[] getBytes();
}
