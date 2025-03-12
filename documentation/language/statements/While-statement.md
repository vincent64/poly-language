# While-statement
A **while-statement** is a statement that is used to repeatedly execute a piece of code as long as a condition is satisfied.
The condition is verified each time before executing the code.


---


## Syntax
The syntax of a while-statement is as follows :
```poly
while(condition) statementBlock
```

The condition must be a boolean expression.

The statements block is executed as long as the condition is verified.


## Example
Consider the following piece of code inspired from video game development :
```poly
bool isRunning = true;

while(isRunning) {
    input();
    render();
    update();
    
    if(isCloseButtonClicked())
        isRunning = false;
}
```

In this code, the game is rendered and updated as long as a boolean variable `isRunning` is equal to `true`.
The if-statement at the end of the loop sets the value of the variable to `false` if a close button is clicked,
thus stopping the while-statement.


---


## Related
### [Do-while-statement](Do-while-statement.md)
### [For-statement](For-statement.md)