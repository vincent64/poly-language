# Operation
An **operation** is a function (in the mathematical definition) that takes one or several inputs and produce an output.
The _operator_ is the symbol that represents an operation, and the _operands_ represent the inputs.

There exists several types of operations and operators.

Operations that require a single operand are called _unary operations_.
Those who require two operands are called _binary operations_.
For the ternary operator, see [if-expression](If-expression.md).


---


## Syntax


### Binary operation
A binary operation is an operation that has two operands.
When applying a binary operation, the operator must be placed between the two operands, as such :
```poly
a + b
```


### Unary operation
A unary operation is an operation that has only one operand.
When applying a unary operation, the operator must be placed before the operand (exception for post-increment and -decrement), as such :
```poly
-a
```


## Operations


### Mathematical
Mathematical operations are operations applied on numbers.
The following table describes every mathematical operation :

| Operation          | Operator | Operands | Notes                                       |
|--------------------|----------|----------|---------------------------------------------|
| Addition           | `+`      | 2        | Commutative, associative                    |
| Subtraction        | `-`      | 2        |                                             |
| Multiplication     | `*`      | 2        | Commutative, associative                    |
| Division           | `/`      | 2        | Division by 0 will produce a runtime error. |
| Modulo (remainder) | `%`      | 2        |                                             |
| Negation           | `-`      | 1        |                                             |



### Logical
Logical operations are operations that produces a boolean result.
The following table describes every logical operation :

| Operation                | Operator | Operands | Notes                                                                                                  |
|--------------------------|----------|----------|--------------------------------------------------------------------------------------------------------|
| Equality                 | `==`     | 2        |                                                                                                        |
| Inequality               | `!=`     | 2        |                                                                                                        |
| Greater than             | `>`      | 2        |                                                                                                        |
| Less than                | `<`      | 2        |                                                                                                        |
| Greater or equal than    | `>=`     | 2        |                                                                                                        |
| Less or equal than       | `<=`     | 2        |                                                                                                        |
| Logical AND              | `&&`     | 2        |                                                                                                        |
| Logical AND              | `\|\|`   | 2        |                                                                                                        |
| Logical NOT              | `!`      | 1        |                                                                                                        |
| Reference equality       | `===`    | 2        | Returns `true` if both object have the reference (i.e. they are exactly the same). Not overloadable.   |
| Reference inequality     | `!==`    | 2        | Not overloadable.                                                                                      |
| Instance type equality   | `==:`    | 2        | Returns `true` if the given object is the same type or a subtype of the given class. Not overloadable. |
| Instance type inequality | `!=:`    | 2        | Not overloadable.                                                                                      |



### Bitwise
Bitwise operations are operations that manipulate the bits of a number.
The following table describes every bitwise operation :

| Operation              | Operator | Operands | Notes                                                                                                              |
|------------------------|----------|----------|--------------------------------------------------------------------------------------------------------------------|
| Bitwise AND            | `&`      | 2        |                                                                                                                    |
| Bitwise OR             | `\|`     | 2        |                                                                                                                    |
| Bitwise XOR            | `^`      | 2        |                                                                                                                    |
| Bitwise NOT            | `~`      | 1        |                                                                                                                    |
| Shift left             | `<<`     | 2        |                                                                                                                    |
| Shift right            | `>>`     | 2        |                                                                                                                    |
| Shift right arithmetic | `>>>`    | 2        | Unlike Java, this operator perform the arithmetic right shift. This is done intentionally to align with C and C++. |



### Spaceship
The _spaceship_ (also called _comparison_) binary operation is used to compare two numerical values.
It is named this way because its operator, `<=>`, looks like a spaceship (you need imagination).
The spaceship operator always returns an integer value. Both expression must be numerical.

If the first expression is greater than the second, it returns `1`.
On the other hand, if the first expression is smaller than the second, it returns `-1`.
When both values are equal, it outputs `0`.


### Null coalescing
The _null coalescing_ binary operation is used to provide a default value for when an expression is `null`.
Its operator is `??`. Both expression must have the same type.

If the first expression is null, the operation returns the value of the second expression.
In any other cases, it outputs the value of the first expression.



## Precende
The precedence of the operations is given by the table below.
Most precende levels are similar to C-like languages.


| Level | Operation                                      | Operator                                                                   | Associativity |
|-------|------------------------------------------------|----------------------------------------------------------------------------|---------------|
| 1     | Unary post-expression                          | `++`, `--`                                                                 | right-to-left |
| 2     | Unary pre-expression                           | `-`, `!`, `~`, `++`, `--`                                                  | right-to-left |
| 3     | Multiplication, division, modulo               | `*`, `/`, `%`                                                              | left-to-right |
| 4     | Addition, subtraction                          | `+`, `-`                                                                   | left-to-right |
| 5     | Bitwise shift                                  | `<<`, `>>`, `>>>`                                                          | left-to-right |
| 6     | Type equality                                  | `==:`, `!=:`                                                               | left-to-right |
| 7     | Comparison                                     | `>`, `<`, `>=`, `<=`, `<=>`                                                | left-to-right |
| 8     | Equality, reference equality                   | `==`, `!=`, `===`, `!==`                                                   | left-to-right |
| 9     | Bitwise AND                                    | `&`                                                                        | left-to-right |
| 10    | Bitwise XOR                                    | `^`                                                                        | left-to-right |
| 11    | Bitwise OR                                     | `\|`                                                                       | left-to-right |
| 12    | Logical AND                                    | `&&`                                                                       | left-to-right |
| 13    | Logical OR                                     | `\|\|`                                                                     | left-to-right |
| 14    | Null coalescing                                | `??`                                                                       | right-to-left |
| 15    | Assignement, augmented assignement             | `=`, `+=`, `-=`, `*=`, `/=`, `%=`, `&=`, `^=`, `\|=`, `<<=`, `>>=`, `>>>=` | right-to-left |
| 16    | If-expression, sum-expression, prod-expression | `if`, `sum`, `prod`                                                        | right-to-left |


---


## Related
### [Operator overloading](../objects/Operator-overloading.md)