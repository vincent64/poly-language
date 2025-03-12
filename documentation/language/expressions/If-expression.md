# If-expression
An **if-expression** is an expression that is used to conditionally execute an expression when
a given boolean expression (refered to as the _condition_) evaluates to true.
An if-expression is the expression equivalent to an _if-statement_.

In some languages, it is also called the [ternary operator](https://en.wikipedia.org/wiki/Ternary_conditional_operator).


---


## Syntax
The syntax of an if-expression is as follows :
```poly
if(condition) expression else expression;
```
The condition must be a boolean expression.
Furthermore, both expression must be of the same type, and unlike an if-statement, the else clause is mandatory.


## Example
Consider a method which returns the string "even" if the inputted integer is even, and "odd" otherwise :
```poly
fn String evenOrOdd(int n) {
    return if(n % 2 == 0) "even" else "odd";
}
```


---


## Related
### [If-statement](../statements/If-statement.md)