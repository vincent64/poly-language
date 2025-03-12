# Comments
Comments are textual description that are used to describe the functionnality of the code or attach a remark inside the code.
While they are part of the source code, they are ignored by the compiler when compiling the code.

There exists 2 types of comment :
- Inline comments
- Multiline comments


---


## Inline comments
Inline comments are used for short comments that fit on a single line.

To declare an inline comment, two slashes (`//`) must precede the comment.
Every characters following the two slashes on the same line will be considered as a comment.

Below is an example of an inline comment :
```poly
// I am a comment
```

In the example below, the code will not be executed, because it is inside a comment.
```poly
// Console.println("Hello World");
```


## Multiline comments
Multiline comments are used for long comments that fit on several lines.
Generally, commenting out large portions of code is considered a bad practice, and cause the code to be cluttered.

To declare a multiline comment, a slash followed by an asterisk (`/*`) must precede the comment.
A closing `*/` must be added to declare the end of the comment.

Below is an example of a multiline comment :
```poly
/*
    I am a comment
*/
```


---


## Related
### [Docstring](Docstring.md)