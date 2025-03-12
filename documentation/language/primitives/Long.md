# Long
The **long** primitive type represents a number included in the mathematical integer set.
This type is similar to the integer type, except it has twice the amount of bytes/bits,
allowing for a greater range of values.


---


## Values and size
A long is made of 8 bytes (64 bits) and is signed in [Two's complement](https://en.wikipedia.org/wiki/Twos_complement).
The bytes and bits amounts can be obtained from the `long.bytes` and `long.bits` attributes.

The value of a long can range from -2<sup>63</sup> (`-9,223,372,036,854,775,808`) and 2<sup>63</sup>-1 (`9,223,372,036,854,775,807`).
These bounds can be obtained from the `long.min` and `long.max` attributes respectively.


## Declaration
To declare a long variable, the name of the variable must be preceded by the `long` keyword.
```poly
long value = 1L;
```


## Literal representation
The literal representation for a long value is the same as for integer, except the `L` (or `l`) suffix must be added to the literal value.
Refer to the [integer](Integer.md) documentation for the literal representation.


---


## Related
### [Integer](Integer.md)