# Type casting
**Type casting** is the conversion of an expression of a given type to another type.


---


## Usage
To cast an expression to another type, the expression must be followed by a colon (`:`) and the cast type.
For example, to cast a variable of type `Vector` into a `Matrix` variable :
```poly
Matrix matrix = vector:Matrix;
```

Type casting is also often used for primitive types, for example :
```poly
int value = 50L:int;
```
In this example, a literal long value is cast to an integer value.

Casting can also be applied on arrays, although it is not recommended as it can result in a runtime exception.


## Implicit casting
Implicit casting allows an expression of a given type to be cast without a cast expression.

Implicit casting is performed on primitives when applying a binary operation.
In this case, the primitive with the highest representation is used to perform the operation.

Implicit casting is also performed when up-casting an object to one of its supertypes.

In any other cases, explicit casting is required.


---


## Related
### [Type inference](Type-inference.md)