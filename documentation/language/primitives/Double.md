# Double
The **double** (for _double-precision floating-point_) primitive type represents a number included in the mathematical real set.


---


## Values and size
A double is stored according to the [IEEE-754](https://en.wikipedia.org/wiki/IEEE_754) double-precision floating-point format.
Thus, a double is made of 8 bytes (64 bits).
The bytes and bits amounts can be obtained from the `double.bytes` and `double.bits` attributes.

The value of a double can range from -((2 − 2<sup>−52</sup>) × 2<sup>1023</sup>) up to (2 − 2<sup>−52</sup>) × 2<sup>1023</sup>.
These bounds can be obtained from the `double.min` and `double.max` attributes respectively.
Doubles are the primitives with the greatest values range.


## Declaration
To declare a double variable, the name of the variable must be preceded by the `double` keyword.
```poly
double value = 64.0d;
```


## Literal representation
The literal representation for a double value is the same as for integer, except the `d` (or `D`) suffix must be added to the literal value.
Moreover, floating-point primitives does not support binary or hexadecimal literal representation.
Refer to the [integer](Integer.md) documentation for the literal representation.


---


## Related
### [Float](Float.md)