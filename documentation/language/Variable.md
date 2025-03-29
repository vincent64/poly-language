# Variable
A **variable** is a storage location associated to an identifier, named the _variable name_,
and a type, which defines the kind of values it can hold.
The value of a variable may change during execution, unless it is declared as _constant_.


---


## Declaration
To declare a variable, the variable name must be preceded by the variable type.

For example, a variable of type `Vector` with name `vec` could be declared as such :
```poly
Vector vec;
```

If the variable is constant, the `const` keyword must be placed before the variable type.


## Initialization
When declaring a variable, it is common to initialize it with a given value.
Once a constant variable is initialized, it is impossible to change its value.

To initialize a variable, the variable declaration must be followed by an equal sign (`=`) and the initialization expression.

For example, a variable of type `int` named `variable` can be initialized as such :
```poly
int variable = 256;
```


### Default values
If the variable is not explicitly initialized when declared, it is implicitly initialized to a default value according to its type.
The default value for each type is described in the table below.

| Variable type                | Default value |
|------------------------------|---------------|
| Int, long, byte, short, char | `0`           |
| Float, double                | `0.0`         |
| Boolean                      | `false`       |
| Object, array (reference)    | `null`        |


---


## Related
### [Identifiers](Identifiers.md)
### [Type](Type.md)
### [Field](objects/Field.md)