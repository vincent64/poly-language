# Float
The **float** (short for _floating-point_) primitive type represents a number included in the mathematical real set.


---


## Values and size
A float is stored according to the [IEEE-754](https://en.wikipedia.org/wiki/IEEE_754) floating-point format.
Thus, a float is made of 4 bytes (32 bits).
The bytes and bits amounts can be obtained from the `float.bytes` and `float.bits` attributes.

The value of a float can range from -((2 − 2<sup>−23</sup>) × 2<sup>127</sup>) up to (2 − 2<sup>−23</sup>) × 2<sup>127</sup>.
These bounds can be obtained from the `float.min` and `float.max` attributes respectively.


## Declaration
To declare a float variable, the name of the variable must be preceded by the `float` keyword.
```poly
float value = 32.0f;
```


## Literal representation
The literal representation for a float value is the same as for integer, except the `f` (or `F`) suffix must be added to the literal value.
Moreover, floating-point primitives does not support binary or hexadecimal literal representation.
Refer to the [integer](Integer.md) documentation for the literal representation.


---


## Related
### [Double](Double.md)