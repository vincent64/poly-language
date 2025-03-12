# Integer
The **integer** primitive type represents a number included in the mathematical integer set.
Integers are the most used and the easiest of numbers to work with, and benefits from a wide range of operations.


---


## Values and size
An integer is made of 4 bytes (32 bits) and is signed in [Two's complement](https://en.wikipedia.org/wiki/Twos_complement).
The bytes and bits amounts can be obtained from the `int.bytes` and `int.bits` attributes.

The value of an integer can range from -2<sup>31</sup> (`-2,147,483,648`) up to 2<sup>31</sup>-1 (`2,147,483,647`).
These bounds can be obtained from the `int.min` and `int.max` attributes respectively.


## Declaration
To declare an integer variable, the name of the variable must be preceded by the `int` (short for *integer*) keyword.
```poly
int value = 1;
```


## Literal representation
The exists 3 different numeral systems that can be used to declare an integer literal in the code :
- Decimal (base 10)
- Hexadecimal (base 16)
- Binary (base 2)

Although the most common way to declare a literal integer is using the decimal system,
the binary and hexadecimal representations are often useful when dealing with raw bytes and bits,
as well as when performing bitwise operations.


### Decimal
Declaring an integer literal in decimal is straightforward and does not require any prefix.


### Hexadecimal
To declare a literal integer in hexadecimal, the `0x` prefix must be used, followed by the digits from `0` to `F`.


### Binary
To declare a literal integer in binary, the `0b` prefix must be used, followed by the digits `0` or `1`.


---


## Related
### [Byte](Byte.md)
### [Short](Short.md)
### [Long](Long.md)