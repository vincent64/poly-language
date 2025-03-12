# Nested/inner classes
A **nested class** is a class that is declared inside another class.
This class works like a standalone class, and is used the same way as a non-nested class.

An **inner class** is a nested class that is bound to its _outer class_,
and cannot be instantiated without having an instance of the outer class.


---


## Declaration
A nested (or inner) class is declared inside another class, as such :
```poly
class Vector {
    class Space { }
}
```
In this example, the class `Space` is nested inside the class `Vector`.

To declare a nested class as inner, the `inner` keyword must be used instead of the `class` keyword, as such :
```poly
class Vector {
    inner Space { }
}
```


## Usage
Using a nested class is done the same way as a normal class.
However, the name of a nested class is made of the name of the outer class, followed by a dot (`.`)
and the name of the nested class.
For example, to declare a variable with the type `Space` :
```poly
Vector.Space space = new(...);
```

To use an inner class, an instance of the outer class must already exist.
```poly
Vector vector = new(...);
Vector.Space space = vector.new(...);
```





---


## Related

### [Class](Class.md)
### [Inheritance](Inheritance.md)
