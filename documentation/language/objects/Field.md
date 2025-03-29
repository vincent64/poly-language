# Field
A **field** is a variable that is attached to a class or object.

A field can either be an _instance field_, meaning it can be different for every object,
or be a _static field_, meaning it is attached to the class itself.
Instance fields are often called _attribute_.


---


## Declaration
A field must always be declared before method declarations inside a class.

To declare a class, the field name must be preceded by its type and optional access and behavior modifiers.
Unlike methods and classes, fields do not have a special keyword.

For example, a private integer attribute could be declared this way :
```poly
- int attribute;
```

While a static and constant integer field could be declared this way :
```poly
+ # const int CONSTANT_VALUE;
```

A field can also be initialized. For more information, visit the [variable](../Variable.md) documentation.


## Access & behavior
When declaring a field, you can define from where it can be accessed inside the project,
as well as how the field should behave.
Note that the class behavior modifiers can impact the fields it contains.


### Access modifiers
Access modifiers define from where the field can be accessed and modified.
The access modifier is represented by a single symbol, inspired from the
[UML notation](https://en.wikipedia.org/wiki/Unified_Modeling_Language).
A field can only have a single access modifier.

Below is a table of the possible access modifiers and their description.

| Symbol | Name      | Description                                                                       |
|--------|-----------|-----------------------------------------------------------------------------------|
| _none_ | Default   | The field can only be accessed from other classes in the same package.            |
| `+`    | Public    | The field can be accessed from any classes.                                       |
| `-`    | Private   | The field can only be accessed from inside the class and inner classes.           |
| `~`    | Protected | The field can be accessed from others classes in the same package and subclasses. |

**Mnemonic :** You can think of the `+` as positive, open to everyone, the least restrictive,
and the `-` as negative, closed and the most restrictive. You can think of the `~` as somewhat in the middle.


### Behavior modifiers
Behavior modifiers define how the field should behave.
The behavior modifier is represented by a single symbol or keyword.

Below is a table of the possible behavior modifiers and their description.

| Symbol/Keyword | Name     | Description                                          |
|----------------|----------|------------------------------------------------------|
| `#`            | Static   | The field can only be accessed statically.           |
| `const`        | Constant | The field cannot be modified once it is initialized. |

**Mnemonic :** You can think of the `#` as a brick wall, tight and static.


---


## Related
### [Class](Class.md)
### [Method](Method.md)