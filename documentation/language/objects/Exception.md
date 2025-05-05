# Exception
An **exception** is a class that represents an exceptional event during runtime.
An exception can be thrown by the program during execution, and can also be caught using a [try-catch-statement](../statements/Try-catch-statement.md).

Every exception extends from the `RuntimeException` exception.

In Poly, every exception are unchecked, meaning the compiler does not enforce catching exceptions.


---


## Declaration
To declare an exception, the name of the exception must be preceded by the `exception` keyword, as such :
```poly
exception MyException { }
```

Similar to classes, an exception can extend from another exception, as follows :
```poly
exception StackOverflowException(StackException) { }
```

Please note that the superclass must be an exception or extend from `RuntimeException`.


## Access & behavior
Similar to classes, an exception can have an access modifier.
However, an exception cannot have the static behavior modifier.
Please refer to the [classes](Class.md) documentation for access and behavior modifiers.


---


## Related
### [Class](Class.md)
### [Throw-statement](../statements/Throw-statement.md)
### [Try-catch-statement](../statements/Try-catch-statement.md)