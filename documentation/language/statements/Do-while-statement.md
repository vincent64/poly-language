# Do-while-statement
A **do-while-statement** is a statement that is used to repeatedly execute a piece of code as long as a condition is satisfied.
Unlike a [while-statement](While-statement.md), the condition is verified after executing the code.


---


## Syntax
The syntax of a do-while-statement is as follows :
```poly
do statementBlock while(condition);
```

The condition must be a boolean expression.

The statements block is executed as long as the condition is verified.


## Example
Consider the following piece of code inspired from AI simulation :
```poly
do {
    player.explore();
} while(!player.hasReachedGoal());
```

In this code, a simulated player is exploring its environment as long as it has not reached its goal.
Notice how the first execution of `explore()` is called before the condition is verified.


---


## Related
### [While-statement](While-statement.md)
### [For-statement](For-statement.md)