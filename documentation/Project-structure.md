# Project structure
While having a single code file may be good for educational or testing purposes,
it is most of the time not enough to write interesting projects and applications.

When writing multiple code files, it is important to follow the general structure of a Poly project as described in this section,
in order to have a well-organized and coherent project.


---


## General structure
Because an image speaks a thousand words, below is an example of a simple project structure.

```
MyProjectFolder                                     (Project folder)
    ┝ src                                           (Source folder)
    │   ┕ org.example                               (Packages)
    │       ┝ util
    │       │   ┝ Helper.poly
    │       │   ┕ Utility.poly
    │       │
    │       ┝ Main.poly
    │       ┕ Calculator.poly
    │
    ┝ out                                           (Output folder)
    ┕ libs                                          (Library folder)
```

The name of the various folders can be anything you want.
See the [compilation](Compilation.md) documentation for information on how to define folder names.

### Project folder
In this example, the project folder is named `MyProjectFolder`.



### Source folder
The source folder is named `src`.
This folder is where your code should be, and is also the folder to provide when compiling your code.


#### Packages
Every folder in the source folder is considered to be part of the project "package".
You can have multiple packages in a single source folder.
Most of the time, the package name represents the company name, the individual nickname,
or any descriptive name for the source code files contained inside.

> Please note that unlike in Java, where the package name of a class is defined
> using the `package` keyword, the package name of a Poly class is only defined
> by its location in the source folder.
> In the example above, the package name of the `Main.poly` file is `org.example`.


### Library folder
The library folder is named `libs`. 
This folder should contain only external and third-party libraries, compiled and
compressed as JAR (Java Archive) files.


### Output folder
The output folder is named `out`.
This folder should only contain the generated compiled code files from your source folder.
You should not add, remove or change the files in this folder, as this can cause issue when executing your code.
Generated JAR files are also saved in this folder.