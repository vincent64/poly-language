# For-statement
A **for-statement** (also called _for-loop_) is a statement that is used to iterate (or loop) a piece of code as long a condition is satisfied.
It is often used to iterate through arrays and lists.


---


## Syntax
The syntax of a for-statement is as follows :
```poly
for(statement; condition; expression) statementBlock
```

The condition must be a boolean expression.

The statement is executed once at the very beginning, before iterating.
The statements block is executed as long as the condition is verified.
The expression is executed at the end of every iteration.


## Example
Consider the following method which returns the maximum integer from the given array :
```poly
fn int getMaximum(int[] values) {
    int max = values[0];
    
    for(int i = 0; i < values.size; i++) {
        if(values[i] > max)
            max = values[i];
    }
    
    return max;
}
```
Note: we consider here that the given array has at least a single element.

In this example, a for-statement is used to iterate through every element of the array in order to find a maximum.
The statement is here an integer variable declaration, with initial value set to `0`.
The condition is as long as the variable is smaller than the amount of elements in the array.
Finally, the expression is a variable incrementation.


---


## Related
### [While-statement](While-statement.md)
### [Do-while-statement](Do-while-statement.md)
### [Sum-expression](../expressions/Sum-expression.md)