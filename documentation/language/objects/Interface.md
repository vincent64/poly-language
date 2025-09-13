# Interface
An **interface** is a class that provides a list of method signatures for concrete classes to implement.
An interface alone cannot be instantiated like a class, because it only provides methods to implement.

A class can implement zero, one or several interfaces.
An interface may extend from zero or one interface.


---


## Declaration
To declare an interface, the name of the interface must be preceded by the `interface` keyword, as such :
```poly
interface MyInterface { }
```


## Content
An interface should primarily contain methods.

Every method in an interface can either be bodyless/empty (i.e. with no implementation) or have a default implementation.
A bodyless method must be implemented and given a content by the class implementing the interface.
On the contrary, a method with a default implementation may or may not be overriden, similar to a superclass method.

Every method is implicitely public, and non-public are not permitted.

Furthermore, an interface cannot have instance fields.
Every field declared in an interface are implicitly static and constant.

A method is bodyless if it has a semicolon instead of the curly brackets, as follows :
```poly
    fn int compute(int a, int b);
```

Additionnally, an interface can have operator overloading methods, but no constructor.


## Access & behavior
Similar to classes, an interface can have an access modifier.
However, an interface cannot have a behavior modifier.
Please refer to the [classes](Class.md) documentation for access and behavior modifiers.


---


## Related
### [Inheritance](Inheritance.md)
### [Class](Class.md)
### [Method](Method.md)