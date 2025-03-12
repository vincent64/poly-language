# Docstring
**Docstring** (short for *Documentation string*) refers to the documentation of classes, methods and fields in a project.
While docstring is most often used in libraries, they provide an effective source of in-code documentation for programmers.


---


## General usage
To write a docstring describing a class, method or field, you must use the `¦` character (broken pipe)
above the declaration, on the same indentation level.
Docstring acts like inline comments: every character following the `¦` are considered part of the docstring.

The first paragraph of the docstring should be a concise and straight-forward description of the class, method or field.
The programmer using the class, method or field should be able to tell what it is about within the first lines of the docstring.
More paragraphs can be added to describe how and when to use it.

Docstring appended to fields should not take more than 1-2 line.

Generally, a blank docstring line is left before the declaration, for aesthetic purpose and
to avoid cluttering the code.

Here is an example of a docstring attached to a class :
```poly
¦ The Matrix class represents a mathematical matrix where each component is a real value (a double).
¦ It contains methods to manipulate the matrix, and provides constants for often-used matrix,
¦ such as the identity matrix or the null matrix.
¦ @author Sponge Bob
¦
class+ Matrix {
    ...
}
```

In this example, the docstring provides a concise description of a `Matrix` class,
and describes its main features. The author of the code is also included in the docstring,
using the `@author` tag.

> Docstring is not a replacement for well-written code.
> Naming fields and methods appropriately already is the best of documentation.


## Tags
Tags are short elements that describe quick information about the declaration.
For example, the `@author` tag is used to credit the programmer(s) who wrote the content of the class.

Tags begins with a `@` (arobase) symbol, followed by the tag name. Following this is the content of the tag.

Some tags are class-, method- or field-specific.
This is the case of the `@returns` tag, which can only be used on method declarations.
The following table lists every tags that are used by the language :

| Tag           | Target       | Description                                                                |
|---------------|--------------|----------------------------------------------------------------------------|
| `@author`     | class        | Credits the author of the class content.                                   |
| `@version`    | class        | Describes the version when the class was added into the project.           |
| `@update`     | class        | Describes the version when the class was last updated.                     |
| `@param`      | method       | Describes a parameter in the method declaration and its usage.             |
| `@returns`    | method       | Describes the return value of the method.                                  |
| `@complexity` | method       | Describes the time/space complexity of the code (e.g. for sorting method). |
| `@deprecated` | class/method | Describes a class or method that is deprecated and should not be used.     |
| `@bug`        | class/method | Describes a bug that is present in the code.                               |


## Generating Markdown documentation

> This section is not yet documented and the feature has not yet been implemented.
> It concerns generating Markdown documentation files out of the docstrings in the project.
> It will be implemented in a future version as part of a standalone tool.


---


## Related
### [Comments](Comments.md)