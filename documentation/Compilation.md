# Compilation
The Poly compiler compiles Poly code into Java class files that can then be executed.
Moreover, it is possible to generate a JAR (Java Archive) file from the class files,
which can be easily executed, or distributed as a library.

The Poly compiler is included in the PDK, with the file name `poly`.


---


## Command
The general syntax of the command is described below. The command name is `poly`.
```
poly [project] [parameters] [options]
```


### Project
The project parameter describes the path to the project folder.


### Parameters
The parameters are used to describes the project structure and folders.
The general syntax for project parameters is as such : `[parameter] [value]`.

The path provided in the parameters must be relative to the project folder.

The different parameters and their functionnality are described in the table below :

| Parameter       | Default | Description                                    |
|-----------------|---------|------------------------------------------------|
| `--src [path]`  | `src`   | Sets the path to the project's source folder.  |
| `--out [path]`  | `out`   | Sets the path to the project's output folder.  |
| `--libs [path]` | `libs`  | Sets the path to the project's library folder. |

> The output folder will be overwritten if it already exists and has content.


### Options
The options are parameters that are used to control or provide the compilation process.
As their name suggest, they are optionnal, and can be enabled or disabled.

The various options and their functionnality are described in the table below :

| Parameter   | Default | Description                                                                                     |
|-------------|---------|-------------------------------------------------------------------------------------------------|
| `-warnings` | true    | Displays messages for code warnings.                                                            |
| `-verbose`  | false   | Displays messages related to the compilation process.                                           |
| `-optimize` | false   | Optimizes the output code. Enabling this option may result in slightly longer compilation time. |
| `-jar`      | false   | Produces a JAR file with the output code.                                                       |

> If selected, the JAR option will generate a JAR file out of the compiled code.
> It is good to generate libraries quickly and without hassle.
> However, if you want more complex JAR files, use the `jar` tool included in the Java Development Kit.


## Errors
If there is an issue in your code, the compiler will produce a compile-time error and halt the compilation.
These errors are displayed in red and point the location of the error in your code.


### Codes
`1` General error\
`2` Syntax error\
`3` Class resolution error\
`4` Semantic error\
`9` Limitation error


---


## Related
### [Execution](Execution.md)