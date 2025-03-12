# Class
A **class** is a scheme (or blueprint) that defines the state and behavior of an object.
It provides the variables and functions that dictates how an object behaves.


---


## Declaration
A class can be declared either in a source code file, or inside another class.
Multiple classes can be declared in a same file.
The naming convention for class names is [CamelCase](https://en.wikipedia.org/wiki/Camel_case).

To declare a class, the name of the class must be preceded by the `class` keyword, as such :
```poly
class MyClass { }
```

What's inside the curly brackets is called the _class content_.


## Content
A class can contain 3 different structure inside itself :
- [Fields](Field.md) (variables, represent the class state)
- [Methods](Method.md) (functions, used to perform procedures)
- [Nested/inner classes](Nested-inner-class.md)

The declaration of these must be done in the same order as written above.
Poly forces you to declare them in this order for practical reasons, as well as to improve
readability and maintainability.
Please note that the declaration order does not change the way fields, methods and inner classes interact.


### Constructor
The constructor is a special method that is called when an object of the class is instanciated.
This method should contain the code necessary to initialize the object state.

The constructor can be declared using the `constructor` keyword, which replaces the method name and return type :
```poly
    fn constructor() {
        //Initialize the object state
    }
```

Like any method, the constructor can have any amount of parameters, and can be overloaded.
Furthermore, it is possible to initialize an uninitialized constant field inside the constructor.

It is possible to call another constructor from the current constructor using `this(...);`.


### Destructor
In Poly, there is no explicit destructor.
This is a deliberate choice, as a destructor can cause major issues, such as ghost references.


## Access & behavior
When declaring a class, you can define from where it can be accessed inside the project, as well as how the class should behave.


### Access modifiers
Access modifiers define how the class can be accessed.
The access modifier is represented by a single symbol, inspired from the
[UML notation](https://en.wikipedia.org/wiki/Unified_Modeling_Language).
A class can only have a single access modifier.
The modifier symbol should be placed right after the `class` keyword, conventionally without any space in-between.

A top-level class can only possess one of the first two modifiers, while nested and inner classes can have any.

Below is a table of the possible access modifiers and their description.

| Symbol | Name      | Description                                                       |
|--------|-----------|-------------------------------------------------------------------|
| _none_ | Default   | The class can be accessed from other classes in the same package. |
| `+`    | Public    | The class can be accessed from any other classes in the project.  |
| `-`    | Private   | The class can only be accessed from the current class.            |
| `~`    | Protected | The class can be accessed from other classes in the same package. |

**Mnemonic :** You can think of the `+` as positive, open to everyone, the least restrictive,
and the `-` as negative, closed and the most restrictive.
You can think of the `~` as somewhat in the middle.


### Behavior modifiers
Behavior modifiers define how the class should behave.
The behavior modifier is represented by a single symbol or keyword.
The modifier symbol/keyword should be placed after the `class` keyword and access modifier.

Below is a table of the possible behavior modifiers and their description.
If several modifiers are used, they should be placed in the same order as provided in the table.

| Symbol/Keyword | Name     | Description                                                                                                                |
|----------------|----------|----------------------------------------------------------------------------------------------------------------------------|
| `#`            | Static   | The class cannot be instanciated as an object. It cannot have a constructor. Every method and field are implicitly static. |
| `const`        | Constant | The class cannot have subclasses. Every method and field are implicitly constant.                                          |

**Mnemonic :** You can think of the `#` as a brick wall, tight and static.


---


## Related

### [Inheritance](Inheritance.md)
### [Interface](Interface.md)
### [Nested/inner class](Nested-inner-class.md)