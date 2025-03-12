# Objects
An **object** is an instance of a type that encapsulates a state and a behavior.
The state and behavior of an object is defined by the developer using a class.


---


## Declaration
To declare an object of a given class, the name of the class must be followed by the variable name, as such :
```poly
Matrix matrix;
```
In this example, the class name is `Matrix` and the variable name `matrix`.


## Instanciation
A class must have an accessible constructor to be instanciated as an object.
To instanciate an object, the `new` keyword must be followed by the class name, and constructor arguments inside parenthesis, as such :
```poly
new Matrix(3, 2);
```


## Accessing
An object can be accessed to retrieve one of its attributes or call one of its methods.
To access an object, the object reference must be followed by a dot (`.`) and the symbol to access.

```poly
matrix.rows         // Access 'rows' field
matrix.print();     // Call 'print()' method
```

To access a static symbol in a class, the class name must be used instead.


---


## Related
### [Class](Class.md)
### [Field](Field.md)
### [Method](Method.md)