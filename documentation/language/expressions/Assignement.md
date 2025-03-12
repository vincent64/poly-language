# Assignement
An **assignement** is an expression used to assign a value to a variable.
The resulting value of the expression is the value that was assigned to the variable.

There exists two kinds of assignement, namely simple assignement and augmented assignement.


---


## Usage
The variable must be either a local variable, a field or an array access.


### Simple assignement
A simple assignement simply assigns the value of the expression to the variable.
The assignement can be done one any type, and is not overloadable.


### Augmented assignement
An augmented assignement performs an operation between the variable and the expression and assigns the resulting value to the variable.
The augmented assignement is a binary operation that is overloadable.

For the list of augmented assignements, see [operations](Operation.md).

The example below shows the difference between a simple and an augmented assignement.
Both assignement expressions are perfectly similar, but the second is more concise.
```poly
variable += 16;             // Augmented assignement
variable = variable + 16;   // Simple assignement
```


---


## Related
### [Operation](Operation.md)