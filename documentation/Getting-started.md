# Getting started
This page will help you start coding with the **Poly programming language**.
If you already have experience coding with high-level programming languages such as C, C++, Java or C#,
then this guide will be relatively easy for you.

Before we get started, it is important that you have the following installed :
- [Poly Development Kit (PDK)](Poly-development-kit)
- [Java Runtime Environment (JRE)](https://www.java.com/)


---


## Installing the PDK
Before starting our coding journey, you will need to download the Poly Development Kit (PDK).
This development kit contains many important tools, among which is the Poly compiler and standard library.
Make sure to install the latest version.


## Hello World
> "_Your first podcast will be awful. Your first video will be awful.
> Your first article will be awful. Your first art will be awful.
> Your first photo will be awful. Your first game will be awful.
> But your first code will be perfect. Zero bugs and a very clean code.
> It will be 'Hello World'._"
> 
> ~ _Unknown_ (2019)


### Creating a file
To start coding in Poly, you have to create a file, with whatever name you want.
The file extension for the Poly programming language is `.poly`.

In this example, we are going to use the `HelloWorld.poly` file name.


### Declare a main class
Because Poly is an object-oriented programming language, you have to create a main class before writing code.
The class documentation is available [here](language/objects/Class.md), but for now, you can use the following template :
```poly
class+ # HelloWorld {
    
}
```


### Declare the main method
Your Poly code needs an entry point to be executed. This entry point is referred to as the _main method_.
The documentation for methods is available [here](language/objects/Method.md), but you don't need for now.
The main method is a special method which can be declared in your main class as such :
```poly
class+ HelloWorld {
    fn+ void main() {
        
    }
}
```

For now, the method body is empty. But you will learn with the next steps to write your very own code in it!


### Print "_Hello World !_"
Printing "_Hello World !_" is relatively straightforward.
It is done using the `println(...);` method from the `Console` class, which can be called anywhere in the code.

This method takes as only argument a string (or text).
In order to have a literal string, we need to add a `"` before and after the text.

Printing hello world in our first Poly code would look like this :
```poly
class+ HelloWorld {
    fn+ void main() {
        Console.println("Hello World !");
    }
}
```

Note that Poly is a semicolon-based language.
Therefore, it is mandatory to write a semicolon at the end of every statement.


### Compiling the code
Now that we have our source code ready, we can go ahead and compile it.
The compiler will take our source code file and generate a file called `HelloWorld.class`,
or whatever name you named your file instead of `HelloWorld`.

Compiling the code is done using the command interface with the following command :
````
poly HelloWorld.poly
````

Make sure that the command interface is currently set in the directory of the compiler executable.
You may also need to specify the full path of the file instead.


### Executing the code
This is the final step in our coding journey.
To execute the code, we make use of the Java Virtual Machine, included in the JRE.

The command to execute the compiled code is as follows :
````
java -cp ";polylib.jar" HelloWorld.class
````

Again, make sure the command interface is in your current working directory.
You may also need to replace `polylib.jar` with the full path to the PDK's library file.

On you executed the command, you should see :
````
Hello World !
````

Congratulations! You wrote your first piece of Poly code ever.


---


## Dive deeper
Now that you know how to write code in the Poly programming language, you might want to learn more about it.
The whole documentation is available here.

For questions, you may use StackOverflow.


















