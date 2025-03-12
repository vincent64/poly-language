# If-statement
An **if-statement** is a statement that is used to conditionally execute a block of code when
a given boolean expression (refered to as the _condition_) evaluates to true.

An if-statement can also have an _else clause_ that can be used to execute a block of code
when the condition does not evaluate to true.


---


## Syntax
The syntax of an if-statement is as follows :
```poly
if(condition) statementBlock
```

The condition must be a boolean expression.

It is also possible to add an else cause, as such :
```poly
if(condition) statementBlock
else statementBlock
```

> It is generally considered bad practice to have more than 3 chained if-else-if-statement.
> Instead, you can make use of the [match-statement](Match-statement.md), which offers greater readability.


## Example
Consider a piece of code which prints a different message according to the value of a `temperature` integer :
```poly
if(temperature < 0) {
    Console.println("Freezing cold.");
} else if(temperature > 100) {
    Console.println("Melting hot.");
} else {
    Console.println("Liveable temperature!");
}
```

Here, the code prints "Freezing cold." whenever the temperature is below 0°C,
"Melting hot." when the temperature is above 100°C and "Liveable temperature!" in other cases.


---


## Related
### [If-expression](../expressions/If-expression.md)
### [Switch-statement](Switch-statement.md)
### [Match-statement](Match-statement.md)