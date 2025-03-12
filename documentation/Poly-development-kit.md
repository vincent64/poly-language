# Poly Development Kit
The **Poly Development Kit** (PDK) is a software development kit which includes
the _Poly compiler_, the _Poly standard library_ and a copy of the _Poly documentation_.

This kit is needed to develop applications and software with the Poly programming language.


---


## Content


### Compiler
The Poly compiler is included in the PDK, with `poly` as file name.
The extension of the file may vary according to your operating system (e.g. `poly.exe` on Windows).


### Standard library
The Poly standard library is included in the PDK under the file name `polylib.jar`.
This file is used by the compiler and the JVM to load dependencies.


### Documentation
A copy of the full Poly documentation is also included in the PDK, for offline usage.
You can either read the full documentation PDF file, or read the Markdown files using a Markdown reader.


### Miscellaneous
The PDK also include a copy or the README file, the LICENSE file.
There may be other files, which are not described here.
The LICENSE file should be read carefully.

> Editing, deleting or moving files inside the PDK may cause major issue for the compiler and your projects.
> You may add automatic compilation scripts inside the PDK folder, as long as it does not harm the other files.
> The PDK folder is not where your projects should live.


## Installation
The PDK can be downloaded directly from the [Poly website](https://poly-language.dev/) or the [GitHub repository](https://github.com/vincent64/poly-language/releases).
Make sure to select the latest version to benefits from the new features and updates.

Once you have downloaded a copy of the PDK, you can extract its content in the directory of your choice.
It is recommended to extract it in your user folder.

If you are using Windows, you can add the path of the compiler file to the `PATH` environment variable of your computer.


---


## Related
### [Compilation](Compilation.md)
### [Execution](Execution.md)