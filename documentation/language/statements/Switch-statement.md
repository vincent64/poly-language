# Switch-statement
A **switch-statement** is a statement that is used to conditionnaly execute a piece of code according to the value of a variable.


---


## Syntax
The syntax of a switch-statement is as follows :
```poly
switch(variable) {
    case(value) statementBlock
    case(value) statementBlock
    ...
}
```

The variable and values must be either an integer type or a string.
The values must be literal values.

A switch-statement can have any amount of cases.


## Example
Consider the following piece of code, which prints a message according to an [HTTP result code](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes) integer `resultCode` :
```poly
switch(resultCode) {
    case(200) Console.println("Success");
    case(404) Console.println("Not found");
    case(403) Console.println("Forbidden");
    case(418) Console.println("I'm a teapot");
    case(500) Console.println("Server issue");
}
```


---


## Related
### [Match-statement](Match-statement.md)
### [If-statement](If-statement.md)