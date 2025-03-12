# Importations
An **importation** (or _import-statement_) is a statement at the very beginning of a source code file
that is used to import a class from another package.

Importations are also a solution to manage several classes with the same name in a project.
The _qualified name_ of a class solves this issue by giving a unique name to each class.


---


## Terminology


### Class name
When declaring a class, the name provided after the `class` keyword is referred to as the _class name_.
In the example below, the class name is `MyClass` :
```poly
class+ MyClass { }
```


### Class package name
The _package name_ of a class is the path of the class declaration starting from the source root.
For example, if the path from the source root to the class declaration was `org/example/MyClass.poly`,
the package name would be `org.example`.

Please note that in Poly, folder separators are replaced by dots (`.`) to make the package name.


### Class qualified name
The _qualified name_ of a class is the package name and the class name altogether.
Following the examples above, the qualified name of the class would be `org.example.MyClass`.

If there was a nested or inner class named `Inner` inside `MyClass`, its qualified name would be `org.example.MyClass.Inner`.


## Usage
When using a class that belongs to another package from the current one,
the class must be imported through an import statement

To import a class, the qualified name of the class must be preceded by the `import` keyword
at the top of the file, before any class declaration.

In the example below, the `Matrix` class is imported using its qualified name :
```poly
import org.example.Matrix;

class+ MyClass {
    - Matrix matrix;
}
```


### Implicit importation
The `Object` class and the `String` class are implicitly imported in every source code file.


### Clashing names
In the uncommon scenario where two different classes with the same name should be used,
these classes must be used using their full qualified name.
Therefore, the import statement can be omitted :
```poly
class+ MyClass {
    - org.example.Matrix matrix;
}
```

> This solution should be used only when using the class name is not possible.


---


## Related
### [Classes](classes/Classes.md)