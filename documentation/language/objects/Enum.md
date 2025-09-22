# Enum
An **enum** is a class that contains a set of constants.
These constants represent an instance of the enum class and can hold attributes.


---


## Declaration
To declare an enum, the name of the enum must be preceded by the `enum` keyword, as such :
```poly
enum MyEnum { }
```

```poly
enum Mineral {
    COPPER,
    MAGNESIUM,
    ZINC;
}
```
In this example, `COPPER`, `MAGNESIUM` and `ZINC` are instances of the `Mineral` enum.

It is possible to give attributes to enum constants by declaring fields and the corresponding constructors in the enum, as follows :
```poly
enum Mineral {
    COPPER(29, "Cu"),
    MAGNESIUM(12, "Mg"),
    ZINC(30, "Zn");
    
    + const int atomicNumber;
    + const String symbol;
    
    fn constructor(int atomicNumber, String symbol) {
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
    }
}
```
The attributes can reference only static fields and methods.

Unlike classes, an enum cannot have a superclass.
An enum can, however, implement interfaces.

Every constructor is implicitly private.



## Access & behavior
Similar to classes, an enum can have an access modifier.
However, an enum cannot have the static behavior modifier.
Please refer to the [classes](Class.md) documentation for access and behavior modifiers.

Every enum constructor must be private or implicitly private.
An implicitly private constructor is a constructor with no access modifier (default).


---


## Related
### [Class](Class.md)
### [Interface](Interface.md)
### [Exception](Exception.md)