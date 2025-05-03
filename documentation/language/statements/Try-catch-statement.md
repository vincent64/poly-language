# Try-catch-statement
A **try-catch-statement** is a statement used to handle exceptions thrown from a block of code.
If an exception is thrown from the block of code, the _handler_ will catch it and will execute the block of code inside the _catch clause_.



---


## Syntax
The syntax of a try-catch-statement is as follows :
```poly
try statementBlock catch(exceptionParameter) statementBlock
```

The exception parameter must be a parameter with the type of the exception to catch.
The exception type must be a subtype of the `Throwable` class.
If the exception is not assignable to the parameter type, the exception is not caught.


## Example
Consider a piece of code which checks if a file exists on the current device :
```poly
fn bool doesFileExist(String name) {
    File file = new(name);
   
    try {
        return file.exists();
    } catch(Exception e) {
        Console.println("Cannot read file, permission denied.");
        return false;
    }
}
```

Here, calling the `exists()` method on the file might throw an exception if the file permission does not allow the program to read this file.
If this is the case, the try-catch-statement catch the error, prints that an error occured, and returns `false`.


---


## Related
### [Throw-statement](Throw-statement.md)