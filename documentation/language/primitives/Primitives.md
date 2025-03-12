# Primitives
A **primitive** is a fundamental type that represents a single basic value with a fixed size.
These types are embedded in the language, and use reserved keywords for declarations.


---


## Types
There exists 8 different primitive types, each representing a different value, with a different range and size.
- [Boolean](Boolean.md)
- [Integer](Integer.md)
- [Float](Float.md)
- [Long](Long.md)
- [Double](Double.md)
- [Byte](Byte.md)
- [Short](Short.md)
- [Character](Character.md)


## Attributes
Every primitive type has 4 attributes attached to them.

The first two attributes represent the amount of _bytes_ and _bits_ the primitive uses in memory.
For example, the `int` primitive is made up of 4 bytes, or 32 bits.
The value of the bytes and bits attributes is of type integer.

The other two attributes represent the minimum and maximum value that the primitive can have.
For example, the `byte` primitive has the minimum value `-256`, and the maximum value `255`.
The value of the minimum and maximum attributes is of the same type as the given primitive.
This means in the case of a `byte`, the minimum and maximum values are of byte type.

To obtain the attribute of a primitive, the attribute name must be followed by the primitive keyword and a `.` (dot).
For example, with the type `byte`:

```poly
byte.bytes      // 1        (type: int)
byte.bits       // 8        (type: int)
byte.min        // -256     (type: byte)
byte.max        // 255      (type: byte)
```

> The `bytes` attribute is similar to the `sizeof()` method in C/C++.


---


## Related
### [Classes](../classes/Classes.md)
### [Type casting](../expressions/Type-casting.md)