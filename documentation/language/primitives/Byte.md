# Byte
The **byte** primitive type represents a number included in the mathematical integer set.
Like its name suggest, it holds a single byte (8 bits) of binary data.


---


## Values and size
A byte is made of 1 byte (8 bits) and is signed in [Two's complement](https://en.wikipedia.org/wiki/Twos_complement).
The bytes and bits amounts can be obtained from the `byte.bytes` and `byte.bits` attributes respectively.

The value of a byte can range from -2<sup>7</sup> (`-128`) up to 2<sup>7</sup>-1 (`127`).
These bounds can be obtained from the `byte.min` and `byte.max` attributes respectively.


## Declaration
To declare a byte variable, the name of the variable must be preceded by the `byte` keyword.
```poly
byte value = 1;
```


---


## Related
### [Integer](Integer.md)
### [Short](Short.md)