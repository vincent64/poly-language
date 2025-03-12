# Outer reference
The `outer` keyword is used to get the reference of the object's outer class inside its class.
Similarly to the `this` keyword, it can be used to call methods, access fields, etc.

The `outer` reference can only be used inside an inner class.
Furthermore, the `outer` reference cannot be used inside a static method, and cannot be used inside a constructor before the object is initialized.


---


## Usage
In the example below, the `outer` reference is used to access the `value` field of the `Outer` outer class :
```poly
class Outer {
    int value;
    
    inner Inner {
        fn int getValue() {
            return outer.value;
        }
    }
}
```






---


## Related
### [This reference](This-reference.md)
### [Super reference](Super-reference.md)