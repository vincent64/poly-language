# String
A **string** is an array (or sequence) of characters making up a piece of text.
Strings are commonly used to store human-readable text and information.

The documentation for strings can be viewed [here](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/lang/String.html).


---


## Declaration
A literal string can be declared using quotes (`"`), as such :
```poly
String s = "Hey!";
```


## Operations
Strings benefit from several operations, which makes it convenient to manipulate them.


### Concatenation
Strings can be concatenated with other strings, primitives or objects using the `+` operator.

For example :
```poly
Console.println("Value: " + 256);
```
This example would print the string `Value: 256`.


### Repeating
A string can be repeated several time using the `*` operator.
The second operand must be an integer.

For example :
```poly
Console.println("Hello " * 3);
```
This example would print the string `Hello Hello Hello `.


### Character access
To access the character at a given index in a string, it is possible to use the usual array access expression.

For example :
```poly
char c = "Congratulations"[3];
```
In this example, the value of the variable `c` would be `g`.


---


## Related
### [Class](Class.md)
### [Operator overloading](Operator-overloading.md)
### [Array](Array.md)