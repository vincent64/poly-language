# Assert-statement
An **assert-statement** is a statement that is used to verify the arguments of a method.
If the condition inside an assert-statement is not satisfied, it will throw an exception.

Assert-statements are generally added at the very beginning of a method's body.


---


## Syntax
The syntax of an assert-statement is as follows :
```poly
assert(condition);
```

The condition must be a boolean expression.


## Example
Consider a method which outputs the factorial of an integer number `n` :
```poly
fn int factorial(int n) {
    return if(n == 0) 1 else prod(int i = n; i > 1; i--) i;
}
```

Although the algorithm to compute the factorial is correct, if the argument `n` has a negative value, the method will return `0`.
However, the mathematical definition of a factorial explicitely mentions that `n` must be non-negative.

To counter this problem, it is possible to add an assert-statement before the return-statement, as such :
```poly
fn int factorial(int n) {
    assert(n >= 0);

    return if(n == 0) 1 else prod(int i = n; i > 1; i--) i;
}
```

This way, if the integer `n` is negative, an exception will be thrown.


---


## Related
### [If-statement](If-statement.md)