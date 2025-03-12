# Boolean
The **boolean** primitive type represents a value that can be either true or false.
This type is essential to conditional statements and comparisons.


---


## Values
A boolean variable can only have one of the two possible values: `true` or `false`.


## Declaration
To declare a boolean variable, the name of the variable must be preceded by the `bool` (short for *boolean*) keyword.
```poly
bool value = true;
```


## Operations
Boolean variables supports only 3 different operations, refered to as _logical operations_.
Applying a logical operation between two boolean values will always produce a boolean result.
Moreover, logical operations short-circuit.

The 4 operations and their associated operator are :

- AND (`&&`)
- OR (`||`)
- NOT (`!`)

The resulting boolean value for each operation can be described by the following truth table :

| `A`     | `B`     | `A && B` | `A \|\| B` | `!A`    |
|---------|---------|----------|------------|---------|
| `false` | `false` | `false`  | `false`    | `true`  |
| `true`  | `false` | `false`  | `true`     |         |
| `false` | `true`  | `false`  | `true`     |         |
| `true`  | `true`  | `true`   | `true`     | `false` |


---


## Related
### [If-statement](../statements/If-statement.md)