# Character
The **character** primitive type represents an ASCII character or a single 16-bit [Unicode](https://en.wikipedia.org/wiki/Unicode) character.
This type is primarily used for strings and files content.


---


## Values and size
A character is made of 1 byte (8 bits) and is unsigned.
The bytes and bits amount can be obtained from the `char.bytes` and `char.bits` attributes.

The value of a character can range from -2<sup>7</sup> (`-128`) and 2<sup>7</sup>-1 (`127`).
These bounds can be obtained from the `char.min` and `char.max` attributes.


## Declaration
To declare a character variable, the name of the variable must be preceded by the `char` (short for _character_) keyword.
```poly
char value = 'a';
```


## Literal representation
A character literal value can be represented in two different ways :
- Single ASCII character
- Integer literal value


### Single character
To declare a literal character, the ASCII character must be surrounded by a pair of apostrophe (`'`).


### Integer value
To declare a character using an integer literal, refer to the [integer](Integer.md) literal representation.


## Escaping
Character escaping provides a way to use special characters. It is done using the slash (`\ `) character.
Below is the list of every character escapes :

- Backslash (`\\`)
- New line (`\n`)
- Tabulation (`\t`)
- Backspace (`\b`)
- Return (`\r`)
- Formfeed (`\f`)
- Quote symbol (`\"`)
- Apostrophe symbol (`\'`)


---


## Related
### [Byte](Byte.md)
### [Integer](Integer.md)