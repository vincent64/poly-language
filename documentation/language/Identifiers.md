# Identifiers
An **identifier** is a name that is given to an entity (or symbol) to make it identifiable.
An identifier should be descriptive and should be made as unique as possible to avoid confusion.


---


## Allowed characters
An identifier can contain any lower- or uppercase letters from the Latin alphabet (`a-z` and `A-Z`),
numerical digits (`0-9`) and underscores (`_`).
However, an identifier must always start with a letter or an underscore.

Identifier starting with a digit is considered a literal numerical value, and will produce an error during compilation.


## Conventions
In Poly, an identifier does not induce any functionnality.
However, there exists a set of conventions that should be followed to name different entities or symbols.
These conventions allow the programmer to automatically infer the kind of the symbol from its identifier.


### Class name
A class name should be in [UpperCamelCase](https://en.wikipedia.org/wiki/Camel_case) (also called PascalCase).
For example : `MatrixBuilder`.


### Method name
A method name should be in [camelCase](https://en.wikipedia.org/wiki/Camel_case).
For example : `setComponent`.


### Variable/field name
There is two separate conventions for variable names :
- If the variable is a constant, the name should be in [SCREAMING_SNAKE_CASE](https://en.wikipedia.org/wiki/Snake_case) (also called ALL_CAPS).
For example : `CONSTANT_VALUE`.
- In any other cases, the name should be in [snake_case](https://en.wikipedia.org/wiki/Snake_case).
For example : `my_value`.


### Package name
The convention for package names is to be in flatcase (full lowercase).
For example : `util`.
Furthermore, package name should not be made of several words (e.g. `myutilitypackage`).


---


## Related
### [Comments](Comments.md)