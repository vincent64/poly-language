# Break-statement
A **break-statement** is a statement that is used to exit a loop.
It can only be used inside a [for-statement](For-statement.md), [while-statement](While-statement.md) or [do-while-statement](Do-while-statement.md).

Because a break-statement is a jump statement, there cannot be any statement following it in the same scope.


---


## Syntax
The syntax of a break-statement is as follows :
```poly
break;
```


## Example
Consider a piece of code which performs a [linear search](https://en.wikipedia.org/wiki/Linear_search) on an array of strings using a for-statement :
```poly
bool isFound = false;

for(int i = 0; i < array.size; i++) {
    if(array[i] == "cherry") {
        isFound = true;
        break;
    }
}
```

In this code, once the string "cherry" is found inside the array, `isFound` is flipped to true,
and the brack-statement then jumps out of the loop using the break-statement.


---


## Related
### [Continue-statement](Continue-statement.md)
### [While-statement](While-statement.md)