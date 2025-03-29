# Inheritance
**Inheritance** allows a class to inherit the functionnality of another class.
This class is said to extend from its _superclass_.
From the point of view of the superclass, this class is called a _subclass_.

Inheritance is _transitive_, meaning if a class `A` extends from `B`, and `B` extends from `C`, then `A` extends from `C`.

In Poly, a class can inherit from a single class, unlike in C++ where _multiple inheritance_ is permitted.
This is a deliberate design choice, as multiple inheritance can cause issues and lead to problems that are hard to track down.
_Interfaces_ can be used to provide similar features.

> Every class inherit from the `Object` class.


---


## Usage
Inheritance is declared along the class declaration.
The name of the superclass must be added after the class name, inside parenthesis, as such :
```poly
class Platypus(Animal) { }
```
In this example, the class `Platypus` extends, or inherits, from `Animal`.

**Mnemonic :** The parenthesis acts like a way of saying a `Platypus` is an `Animal`.

If there is no superclass provided, the default superclass is `Object`.


## Method overriding
When a method with the same signature as a method in the superclass is declared in a class,
this method is said to _override_ the superclass' method.
Method overriding allows for powerful mechanism, such as [dynamic dispatching](https://en.wikipedia.org/wiki/Dynamic_dispatch).

Method overriding only works with instance methods (i.e. methods without the static modifier).

For a method to be a valid override, the return type of both method must be the same.
Furthermore, the overriding method cannot have a weaker access modifier
(e.g. have a private modifier, while the superclass' method is public).
If the superclass' method is private, the current class' method cannot be an overriding method.


### Constructor overriding
While the constructor is a special method, it can also be overriden.
However, unlike regular methods, an overriding constructor can have a weaker access modifier.

If the superclass of a class does not have a constructor with no arguments, it is required to provide a `super(...);` call.


---


## Related
### [Interface](Interface.md)
### [Class](Class.md)
### [Nested/inner class](Nested-inner-class.md)