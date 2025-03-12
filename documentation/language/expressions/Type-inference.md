# Type inference
**Type inference** is the automatic compile-time deduction (or "guessing") of a type.
Type inference in Poly can only be used when instantiating a class, and works only in a few scenarios.


---


## Variable assignement
When assigning a value to a variable, if the value is a class construction,
the type of the class construction will be inferred from the variable type if there is no construction type provided.

In the example below, the type of the class to be created is infered from the variable :
```poly
Matrix matrix = new(3, 3);
```
Please note that in this example, the class creation will create a class of type `Matrix`.
If you want to initialize the variable with a subclass of `Matrix`, you will need to provide it.
The compiler can't guess that for you.


## Return statement
When returning a value in a return statement, if the return expression is a class construction,
the compiler will infer the type of the class construction from the method return type.

In the example below, the class construction expression in the return statement will create a class of type `Matrix` :
```poly
fn Matrix createSquare(int n) {
    return new(n, n);
}
```


## Casting expression
When casting an expression to a given type, if the expression is a class construction expression,
the type of the class construction will be inferred from the cast type if there is no construction type provided.

In the example below, an instance of a `Matrix` class can be created this way :
```poly
Matrix matrix = new(...):Matrix;
```


---


## Related
### [Type casting](Type-casting.md)
### [Classes](../classes/Classes.md)