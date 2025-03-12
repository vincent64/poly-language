package poly.compiler.generator;

import poly.compiler.analyzer.type.Object;
import poly.compiler.analyzer.type.Primitive;
import poly.compiler.analyzer.type.Type;
import poly.compiler.output.attribute.VerificationType;
import poly.compiler.output.content.ConstantPool;
import poly.compiler.output.content.Descriptor;
import poly.compiler.resolver.symbol.ClassSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * The LocalTable class. This class represents the locals table at any
 * given moment during the runtime process. Unlike the VariableTable class,
 * this class is used only during the code generation for the JVM verifications.
 * When a double entry type is added to the local table, the local count is
 * increased by 2 instead of 1. When this type is later removed from the local table,
 * it is replaced by two top verification type.
 * @author Vincent Philippe (@vincent64)
 */
public class LocalTable {
    private final List<VerificationType> localTypes;
    private final ConstantPool constantPool;
    private int maxCount;
    private int localCount;

    /**
     * Constructs a local table with the given constant pool.
     * @param constantPool the constant pool
     */
    public LocalTable(ConstantPool constantPool) {
        this.constantPool = constantPool;

        //Initialize local types list
        localTypes = new ArrayList<>();
    }

    private void addLocal(VerificationType type) {
        localTypes.add(type);
        localCount += type.isDoubleEntry() ? 2 : 1;

        //Increase max stack size
        if(localCount > maxCount)
            maxCount = localCount;
    }

    /**
     * Adds a local with the given type in the table.
     * @param type the local type
     */
    public void addLocal(Type type) {
        //Add primitive verification type
        if(type instanceof Primitive primitive) {
            addLocal(VerificationType.forPrimitive(primitive));
        }

        //Add object verification type
        else if(type instanceof Object object) {
            addLocal(VerificationType.forObject(object.getClassSymbol(), constantPool));
        }

        //Add array verification type
        else {
            String descriptor = String.valueOf(Descriptor.getDescriptorFromType(type));
            addLocal(VerificationType.forObject((short) constantPool.addClassConstant(descriptor)));
        }
    }

    /**
     * Adds an uninitialized this reference local in the table.
     */
    public void addUninitializedThis() {
        addLocal(VerificationType.forUninitializedThisReference());
    }

    /**
     * Sets the initialized this reference local in the table.
     * @param classSymbol the class symbol
     */
    public void setInitializedThis(ClassSymbol classSymbol) {
        localTypes.set(0, VerificationType.forObject(classSymbol, constantPool));
    }

    /**
     * Removes the given amount of locals from the table.
     * This method will replace the locals by the top verification type.
     * @param count the local count
     */
    public void remove(int count) {
        for(int i = localTypes.size() - count; i < localTypes.size(); i++) {
            if(localTypes.set(i, VerificationType.forTop()).isDoubleEntry())
                localTypes.add(i, VerificationType.forTop());
        }
    }

    /**
     * Returns the local at the given index.
     * @param index the index
     * @return the local
     */
    public VerificationType getLocal(int index) {
        //Compute real index with double entries
        for(int i = 0; i < index; i++) {
            if(localTypes.get(i).isDoubleEntry()) index--;
        }

        return localTypes.get(index);
    }

    /**
     * Returns the local types.
     * @return the local types
     */
    public List<VerificationType> getLocalTypes() {
        return localTypes;
    }

    /**
     * Returns the amount of locals.
     * @return the local count
     */
    public int getCount() {
        return localTypes.size();
    }

    /**
     * Returns the maximum locals count.
     * @return the max locals
     */
    public int getMaxCount() {
        return maxCount;
    }
}
