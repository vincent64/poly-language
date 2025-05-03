# Throw-statement
A **throw-statement** is a statement used to unconditionaly throw an exception.


---


## Syntax
The syntax of the throw-statement is as follows :
```poly
throw expression;
```

The expression must have the type of the exception to throw.
The exception type must be a subtype of the `Throwable` class.


## Example
Consider the following piece of code which pushes an object to a stack data structure.
```poly
fn void push(Object object) {
    if(size >= CAPACITY)
        throw new StackOverflowException();
    
    //Add the object to the stack
}
```

Here, if the current size of the stack reaches its capacity, it will throw a `StackOverflowException` exception.


---


## Related
### [Try-catch-statement](Try-catch-statement.md)