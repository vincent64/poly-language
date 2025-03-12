# Sum-expression
A **sum-expression** (or _summation-expression_) is an expression that is used to compute the sum of an expression.
This expression follows the mathematical definition of a [summation](https://en.wikipedia.org/wiki/Summation).


---


## Syntax
The syntax of a sum-expression is as follows :
```poly
sum(statement; condition; expression) expression;
```
The statement must be a variable declaration.
The condition must be a boolean expression.
Furthermore, the expression must be a numerical type.

The statement is executed once at the very beginning, before computing the sum.
The last expression is executed as long as the condition is verified.
The expression is executed at the end of every iteration.

> A sum-expression will output `0` in case of no iteration.

The given syntax can be expressed in the following mathematical expression :

$$ \sum_{\text{statement, condition}}^{\text{expression}} expression; $$


## Example
Consider the following code, which compute the [Basel problem](https://en.wikipedia.org/wiki/Basel_problem) series :
```poly
int max = 256;
float basel = sum(int k = 1; k < max; k++) 1 / (k * k);
```

The above code translates to the following mathematical summation :

$$ \sum_{k = 1}^{max} \frac{1}{k^2} $$


---


## Related
### [Prod-expression](Prod-expression.md)