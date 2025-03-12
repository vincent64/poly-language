# Array
An **array** is a type that represents an ordered collection of elements of a given type.
The amount of elements in an array is fixed and defined during initialization.

Although arrays represent a type, they are treated as objects.


---


## Declaration
To declare an array, the type of the array must be followed by a pair of square brackets and the variable name, as such :
```poly
int[] array;
```

It is possible to declare an array of array (also called _multidimensional array_), as seen below :
```poly
int[][] array;
```


## Initialization
An array has a fixed size which must be provided when initializing it.
To initialize an array, the keyword `new` must be used with a pair of square bracket :
```poly
new int[SIZE];
```
The size must be an integer expression.

The size of the array can be retrieved by accessing the `size` attribute of the array.


## Accessing
To access an array, the array must be followed by a pair of bracket with the index of the element to access in the array, as such :
```poly
int value = array[index];
```
The index must be an integer expression.


## Assigning
To assign an element at a given index in the array, the usual assignement expression can be used :
```poly
array[index] = value;
```
The index must be an integer expression.

It is also possible to use augmented assignement and increment/decrement operations on an element of the array.


---


## Related
### [For-statement](../statements/For-statement.md)