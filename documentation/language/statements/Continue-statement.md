# Continue-statement
A **continue-statement** is a statement that is used to skip the following statements and continue with the next iteration in a loop.
It can only be used inside a [for-statement](For-statement.md), [while-statement](While-statement.md) or [do-while-statement](Do-while-statement.md).

Because a continue-statement is a jump statement, there cannot be any statement following it in the same scope.


---


## Syntax
The syntax of a continue-statement is as follows :
```poly
continue;
```


## Example
Consider a for-statement that execute a piece of code for each odd value of a variable `i` :
```poly
for(int i = 0; i < 256; i++) {
    if(i % 2 == 0)
        continue;
        
    //Some code to perform on odd values of i
}
```

In this example, every time the integer `i` is even, the following code is skipped,
and the next iteration of the loop is executed.


---


## Related
### [Break-statement](Break-statement.md)
### [While-statement](While-statement.md)