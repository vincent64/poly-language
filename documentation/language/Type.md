# Type
A **type** is a classification that defines the set of possible value and behavior of a structure.

There exists 3 different types :
- [Primitive type](primitives/Primitives.md)
- [Object type](objects/Objects.md)
- [Array type](objects/Array.md)

The Poly programming language is strongly and statically typed,
meaning the types of variables and expressions is resolved at compile-time.
This also means variables cannot be reused for different types.


---


## Primitive
A **primitive** is a fundamental type that represents a single basic value with a fixed size.
These types are embedded in the language, and use reserved keywords for declarations.


## Object
An **object** is an instance of a type that encapsulates a state and a behavior.
The state and behavior of an object is defined by the developer using a class.


## Array
An **array** is a type that represents an ordered collection of elements of a given type.
Although an array is a special kind of object, it is a different type because of its different syntax and usage.


---


## Related
### [Type casting](expressions/Type-casting.md)
### [Type inference](expressions/Type-inference.md)