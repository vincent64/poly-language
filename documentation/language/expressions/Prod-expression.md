# Prod-expression
A **prod-expression** (or _product-expression_) is an expression that is used to compute the product of an expression.
Similarly to the [sum-expression](Sum-expression.md), it follows the mathematical definition of a [product](https://en.wikipedia.org/wiki/Product_(mathematics)).


---


## Syntax
The syntax of a prod-expression is as follows :
```poly
prod(statement; condition; expression) expression;
```
The statement must be a variable declaration.
The condition must be a boolean expression.
Furthermore, the expression must be a numerical type.

The statement is executed once at the very beginning, before computing the product.
The last expression is executed as long as the condition is verified.
The expression is executed at the end of every iteration.

> A prod-expression will output `1` in case of no iteration.

The given syntax can be expressed in the following mathematical expression :

$$ \prod_{\text{statement, condition}}^{\text{expression}} expression; $$


## Example
Consider the following code which compute the exponent of a float `b` to the integer power `e` :
```poly
float exponent = prod(int k = 0; k < e; k++) b;
```

The above code translates to the following mathematical product :

$$ \prod_{k = 0}^{e} b $$


---


## Related
### [Sum-expression](Sum-expression.md)