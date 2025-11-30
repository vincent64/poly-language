package poly.compiler.output.content;

import poly.compiler.output.jvm.Instructions;
import poly.compiler.tokenizer.content.Symbol;
import poly.compiler.util.Character;

/**
 * The AccessModifier enum. This enum contains the 4 possible access modifier for
 * a class, field or method. Each access modifier contains its corresponding symbol
 * in the language, as well as its corresponding instruction byte code.
 * @author Vincent Philippe (@vincent64)
 */
public enum AccessModifier {
    PUBLIC(Symbol.PLUS_SIGN,
            Instructions.ClassAccessFlag.PUBLIC,
            Instructions.FieldAccessFlag.PUBLIC,
            Instructions.MethodAccessFlag.PUBLIC),
    PROTECTED(Symbol.TILDA_SIGN,
            Instructions.ClassAccessFlag.PROTECTED,
            Instructions.FieldAccessFlag.PROTECTED,
            Instructions.MethodAccessFlag.PROTECTED),
    DEFAULT("",
            (short) 0x0000,
            (short) 0x0000,
            (short) 0x0000),
    PRIVATE(Symbol.MINUS_SIGN,
            Instructions.ClassAccessFlag.PRIVATE,
            Instructions.FieldAccessFlag.PRIVATE,
            Instructions.MethodAccessFlag.PRIVATE);

    private final String symbol;
    private final short classAccessFlag;
    private final short fieldAccessFlag;
    private final short methodAccessFlag;

    AccessModifier(String symbol, short classAccessFlag, short fieldAccessFlag, short methodAccessFlag) {
        this.symbol = symbol;
        this.classAccessFlag = classAccessFlag;
        this.fieldAccessFlag = fieldAccessFlag;
        this.methodAccessFlag = methodAccessFlag;
    }

    /**
     * Returns the class access modifier from the given access flag.
     * @param accessFlag the access flag
     * @return the access modifier
     */
    public static AccessModifier fromClassAccessFlag(int accessFlag) {
        if((accessFlag & PUBLIC.getClassAccessFlag()) != 0) return PUBLIC;
        return DEFAULT;
    }

    /**
     * Returns the method access modifier from the given method access flag.
     * @param accessFlag the access flag
     * @return the access modifier
     */
    public static AccessModifier fromMethodAccessFlag(int accessFlag) {
        if((accessFlag & PUBLIC.getMethodAccessFlag()) != 0) return PUBLIC;
        if((accessFlag & PRIVATE.getMethodAccessFlag()) != 0) return PRIVATE;
        if((accessFlag & PROTECTED.getMethodAccessFlag()) != 0) return PROTECTED;
        return DEFAULT;
    }

    /**
     * Returns the field access modifier from the given field access flag.
     * @param accessFlag the access flag
     * @return the access modifier
     */
    public static AccessModifier fromFieldAccessFlag(int accessFlag) {
        if((accessFlag & PUBLIC.getFieldAccessFlag()) != 0) return PUBLIC;
        if((accessFlag & PRIVATE.getFieldAccessFlag()) != 0) return PRIVATE;
        if((accessFlag & PROTECTED.getFieldAccessFlag()) != 0) return PROTECTED;
        return DEFAULT;
    }

    /**
     * Finds and returns the access modifier corresponding to the given symbol.
     * @param symbol the symbol
     * @return the corresponding access modifier
     */
    public static AccessModifier findAccessModifier(String symbol) {
        for(AccessModifier accessModifier : values()) {
            if(accessModifier.symbol.equals(symbol))
                return accessModifier;
        }

        return DEFAULT;
    }

    /**
     * Returns whether the current access modifier is weaker than the given access modifier.
     * @param accessModifier the other access modifier
     * @return true if the current access modifier is weaker
     */
    public boolean isWeakerThan(AccessModifier accessModifier) {
        return this.ordinal() < accessModifier.ordinal();
    }

    /**
     * Returns the class access flag.
     * @return the access flag
     */
    public short getClassAccessFlag() {
        return classAccessFlag;
    }

    /**
     * Returns the field access flag.
     * @return the access flag
     */
    public short getFieldAccessFlag() {
        return fieldAccessFlag;
    }

    /**
     * Returns the method access flag.
     * @return the access flag
     */
    public short getMethodAccessFlag() {
        return methodAccessFlag;
    }
}
