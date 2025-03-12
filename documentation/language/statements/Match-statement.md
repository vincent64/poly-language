# Match-statement
A **match-statement** is a statement that is used conditionnally execute a piece of code according to disjunctive cases.
It can be interpreted like a concise and more readable alternative to chained if-else-if statements.


---


## Syntax
The syntax of a match-statement is as follows :
```poly
match {
    case(condition) statementBlock
    case(condition) statementBlock
    ...
}
```

The conditions must be boolean expressions.

A match-statement can have any amount of cases.


## Example
Consider the following piece of code which compares two given floats `a` and `b` :
```poly
int comparison;

match {
    case(a > b) comparison = 1;
    case(a < b) comparison = -1;
    case(a == b) comparison = 0;
}
```

In this code, the `comparison` integer variable is assigned `1` if `a` is greater than `b`,
`-1` if `b` is greater than `a` and `0` if they are equal.

This example shows a typical usage of a match-statement instead of nested if-statements.
Moreover, match-statements are often used in spaceship operator overload methods.


---


## Related
### [Switch-statement](Switch-statement.md)
### [If-statement](If-statement.md)