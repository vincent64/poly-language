# Short
The **short** primitive type represents a number included in the mathematical integer set.
Unlike an integer, it is made up of only 2 bytes.


---


## Values and size
A short is made of 2 bytes (16 bits) and is signed in [Two's complement](https://en.wikipedia.org/wiki/Twos_complement).
The bytes and bits amounts can be obtained from the `short.bytes` and `short.bits` attributes.

The value of a short can range from -2<sup>15</sup> (`-32,768`) up to 2<sup>15</sup>-1 (`32,767`).
These bounds can be obtained from the `short.min` and `short.max` attributes.


## Declaration
To declare a short variable, the name of the variable must be preceded by the `short` keyword.
```poly
short value = 1;
```


---


## Related
### [Integer](Integer.md)
### [Byte](Byte.md)