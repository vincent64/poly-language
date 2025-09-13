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

The variable and values must be either an integer type, a string or an enum instance.
The values must be literal values.

A switch-statement can have any amount of cases.
Moreover, it is possible to declare a default case using the `else` keyword, as such :
```poly
switch(variable) {
    ... cases ...
    else statementBlock
}
```
The default case is executed when all other defined cases don't match.


## Example
Consider the following piece of code, which prints a message according to an [HTTP result code](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes) integer `resultCode` :
```poly
switch(resultCode) {
    case(200) Console.println("Success");
    case(404) Console.println("Not found");
    case(403) Console.println("Forbidden");
    case(418) Console.println("I'm a teapot");
    case(500) Console.println("Server issue");
    else Console.println("Unknown result code");
}
```


---


## Related
### [Match-statement](Match-statement.md)
### [If-statement](If-statement.md)