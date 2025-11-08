# Foreach-statement
A **foreach-statement** (also called a _foreach-loop_) is a statement that is used to iterate through an array or collection.
It is similar to a [for-statement](For-statement.md), but its usage is preferred when having to iterable through an entire collection.


---


## Syntax
The syntax of a foreach-statement is as follows :
```poly
foreach(variableDeclaration : expression) statement
```

The expression's type must be an array.


## Example
Consider the following example, which prints every element that are even in an integer array `array`.
```poly
int[] array = [3, 1, 4, 1, 5, 9, 2];

foreach(int i : array) {
    if(i % 2 == 0)
        Console.println(i);
}
```
In this example, the foreach-statement iterates through every element of the array, and set the value of `i` to the value of the current element


---


## Related
### [For-statement](For-statement.md)
### [While-statement](While-statement.md)