# Super reference
The `super` keyword is used to get the reference of the current object's superclass inside its class.
Similarly to the `this` keyword, it can be used to call methods, access fields, etc.

It can be used to choose a method or field that has a different implementation or value from the current class.

The `super` reference cannot be used inside a static method, and cannot be used inside a constructor before the object is initialized.
Furthermore, if a class somehow does not have a superclass, using the keyword will produce an error.


---


## Usage
In the example below, the `super` reference is used to access the `health` field of the superclass :
```poly
class Animal {
    int health;
}

class Human(Animal) {
    int health;
    
    fn+ constructor() {
        health = super.health;
    }
}
```


## Implicit reference
If the method to call or the field to access is present in the superclass but not in the current class,
there is no ambiguity, and it is possible to omit the `super` keyword.


---


## Related
### [This reference](This-reference.md)
### [Outer reference](Outer-reference.md)