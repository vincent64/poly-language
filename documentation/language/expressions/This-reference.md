# This reference
The `this` keyword is used to get the reference of the current object inside its class.
It can be used to call methods, access fields and be used as a reference in an expression.

It is commonly used when a field and a local variable have the same name.
Using `this` removes the ambiguity by choosing the class field.

The `this` reference cannot be used inside a static method, and cannot be used inside a constructor before the object is initialized.


---


## Usage
In the example below, the `this` reference is used to set the `value` field the value of the local variable with the same name :
```poly
class Matrix {
    - int value;
    
    fn+ constructor(int value) {
        this.value = value;
    }
}
```

> If you wish to call the current object using the overloaded call operator,
> you have to put the `this` keyword inside parentheses to avoid ambiguity with the `this(...)` constructor.


## Implicit reference
If the method to call or the field to access is present in the same class, it is possible to omit the `this` keyword.


---


## Related
### [Super reference](Super-reference.md)
### [Outer reference](Outer-reference.md)
