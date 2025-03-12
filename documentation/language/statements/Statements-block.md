# Statements block
A **statements block** is a block of code that can contain zero, one, or several statements.
While it is possible to declare a _dangling statements block_,
a statements block is often used in combination with an _if-statement_, or _for-statement_, for example.

A variable declared inside a statement block cannot be used outside of it (i.e. outside its scope).
However, any variable declared before a statement block can be used.


---


## Syntax
The syntax of a statement block is as follows :
```poly
{
    statement
    statement
    ...
}
```

A statement block can have any amount of statements.
If a statement block only has a single statement, it is possible to omit the curly brackets.


---


## Related
### [If-statement](If-statement.md)