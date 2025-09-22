# Method
A **method** (also called **function**) is a procedure that can take as input a set of _parameters_, and can return a result.

The method name and its parameters are called the _signature_ of the method.
Two methods cannot have the same signature in a class.


---


## Declaration
A method can only be declared inside a class.
The naming convention for method names is [camelCase](https://en.wikipedia.org/wiki/Camel_case).

To declare a method, the `fn` keyword must be followed by the _return type_ of the method,
its name, and a list of _parameters_ enclosed by parenthesis, as such :
```poly
fn int myMethod(int a, int b) { }
```

What's inside the curly brackets is commonly called the _method's content_, or _method's body_.
If the method's body contains a single statement, it is possible to omit the curly brackets.


## Parameters
A parameter is a variable that is used inside the method's body and whose value is the value passed as argument when calling the method.
A method can have any amount of parameters, including none.
Similar to a variable, a parameter can be declared constant using the `const` keyword.

An _attribute parameter_ is a parameter that is also used to define a class field with the same name, with the same value.
A parameter can be declared as attribute by using the `attr` keyword before the parameter type.

In the example below, the method declarations are equivalent.
```poly
fn void setValue(attr int value) { ... }
```
```poly
fn void setValues(int value) {
    this.value = value;
    ...
}
```

A parameter cannot be both constant and an attribute.


## Content
A method body is where the code is written.

If the method has a return type that is not `void`, there must be a return statement inside the method content.


## Overloading
_Overloading_ a method is having several methods with the same name in a class,
but with a different set of paramaters.
Overloading a method is often used as a way to have default parameter values.

In the example below, the two method have the same name, but the second is missing the `b` integer parameter.
This method's only purpose is to call the first method, with `7` acting as the default value for `b`.
```poly
fn void doSomething(int a, int b) { }

fn void doSomething(int a) {
    doSomething(a, 7);
}
```


## Access & behavior
When declaring a method, you can define from where it can be accessed in the project,
as well as how the method should behave.
Note that the class behavior modifiers can impact the method it contains.

### Access modifiers
Access modifiers define from where the method can be called.
The access modifier is represented by a single symbol, inspired from the
[UML notation](https://en.wikipedia.org/wiki/Unified_Modeling_Language).
A method can only have a single access modifier.

Below is a table of the possible access modifiers and their description.

| Symbol | Name      | Description                                                                      |
|--------|-----------|----------------------------------------------------------------------------------|
| _none_ | Default   | The method can only be called by other classes in the same package.              |
| `+`    | Public    | The method can be called from any classes.                                       |
| `-`    | Private   | The method can only be called from inside the class and inner classes.           |
| `~`    | Protected | The method can be called from others classes in the same package and subclasses. |

**Mnemonic :** You can think of the `+` as positive, open to everyone, the least restrictive,
and the `-` as negative, closed and the most restrictive. You can think of the `~` as somewhat in the middle.


### Behavior modifiers
Behavior modifiers define how the method should behave.
The behavior modifier is represented by a single symbol or keyword.

Below is a table of the possible behavior modifiers and their description.

| Symbol/Keyword | Name     | Description                                   |
|----------------|----------|-----------------------------------------------|
| `#`            | Static   | The method can only be called statically.     |
| `const`        | Constant | The method cannot be overriden by subclasses. |

**Mnemonic :** You can think of the `#` as a brick wall, tight and static.


## Related
### [Operator overloading](Operator-overloading.md)