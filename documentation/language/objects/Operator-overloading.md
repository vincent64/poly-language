# Operator overloading
**Operator overloading** allows objects to have primitive operations applied onto them.
It is done through methods with special name and syntax.

An operator overloading method can have at most 1 argument (in the case of a binary operation),
or none (in the case of a unary operation).


---


## Declaration
Unlike normal methods, operator overloading methods must start with the `op` (short for _operator_) keyword.
For an operator overloading method, the method name is replaced by the operator symbols.

The return type represents the resulting type after applying the operation.
If the operation is a binary operation, it is applied between the current object and the method argument.
If the operation is a unary operation, it is applied on the current object itself.

The example below illustrates an operator overloading method inside a `Matrix` class :
```poly
op Matrix *(double value) {
    ...
}
```

This operator overloading allows a matrix to be multiplied by a `double` value, and returning the resulting matrix.

Operator overloading methods with more arguments than necessary will simply be ignored.


## Operators
Some operator cannot be overloaded for ovbious reasons, such as the reference equality operator (`===`).
The list below lists every operator that can be overloaded.


### Mathematical
- Addition (`+`)
- Subtraction (`-`)
- Multiplication (`*`)
- Division (`/`)
- Modulo (`%`)
- Negation (`-`)
- Increment (`++`)
- Decrement (`--`)

**Note :** The increment and decrement operation overload can be both postfix or prefix; both will produce the same result.


### Logical
- Equality (`==`)
- Inequality (`!=`)
- Greater (`>`)
- Less (`<`)
- Greater or equal (`>=`)
- Less or equal (`<=`)
- Spaceship (`<=>`)
- Logical AND (`&&`)
- Logical OR (`||`)
- Logical NOT (`!`)

**Note :** When overloading the equality operator, make sure to also override the `equals(Object)` method
for compatibility with the Poly standard library and Java API.


### Bitwise
- Bitwise AND (`&`)
- Bitwise XOR (`^`)
- Bitwise OR (`|`)
- Bitwise NOT (`~`)
- Shift left (`<<`)
- Shift right (`>>`)
- Shift right arithmetic (`>>>`)


### Augmented assignement
- Addition (`+=`)
- Subtraction (`-=`)
- Multiplication (`*=`)
- Division (`/=`)
- Modulo (`%=`)
- Bitwise AND (`&=`)
- Bitwise XOR (`^=`)
- Bitwise OR (`|=`)
- Shift left (`<<=`)
- Shift right (`>>=`)
- Shift right arithmetic (`>>>=`)


### Call
The method call expression can be overloaded to make an object callable like a function, similar to a functor.
The operator method name for object call is `call`.


### Access
The array access expression can be overloaded to make an object accessible like an array.
The operator method name for object access is `access`.


---


## Related
### [Methods](Method.md)
### [Operations](../expressions/Operation.md)