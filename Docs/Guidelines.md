# Indentation

* Use **4 spaces** for each level of indentation

# Naming Conventions

* `methods` and `fields` use **camelCase** (stating off with **lowercase**)
* `constants` are all **UPPERCASE** with words seperated by **underscores** 
* `classes` and `interfaces` USE **CamelCase** (starting off with **capital**)

```Java
private final float MULTI_WORD_CONSTANT = 3;
private int theAnswer = 42;

public static int thisIsAMethod(...) { ... }

public class CoolClass { ... }
```

# Comments
    
* Any complex or obscure written code should contain comments
    * **setters** and **getters** for example do _not_ need comments
* Comments should be written to describe the **current** use of the method
* Method comments should not include terms such as `This method ...`. 
    * Write comments as if you are specifying what the method should do.

* Comments inside method bodies should be written commented using only `single line comments`

```Java
//Calculates how many comments i should have
public void exampleComments(...) {
    // This is a very complex piece of code so i am describing it in english
    complex code...
}
```

* Comments inside of method bodies are used only to describe tricky peieces of code. 

# strings.xml

* All values found in `string.xml` should be **lowercase** and words should be seperated by **underscores** 
    * This is an android convension

```Java
the_string
```

* **ALL** text found in our application (with exception to user specified notes) should be found inside of the `strings.xml`
    * This is an android convension

# { Braces }

* The **opening brace** is always on the **same line** as the method's signature, the class name, or the conditional statement
* the **closing brace** is on its *own line*, with the exception of simple `accessor` and `mutators`

```Java
public void coolMethod(...) {
    ...
    for(int i = 0; i < 10; i++) {
        // logic
    }
}
```

# Method signatures

* Method signatures should only be a space **before** the opening brace

```Java
public int add(int x, int y) {
    return x + y;
}
```

* If there is **overloading**, new parameters are added to the **end** of the signature

```Java
public void doSomething(int x) {
    /// Do something with x
}

public void doSomething(int x, int y) {
    // Do something with both x and y
}
```

# Exception handling/logging

* Exceptions should be caught gracefully. Print any exceptions to **print the stack trace**

# Loops and conditional statements

* Braces should be used around **all statements**, even single statements
    * It makes everything a little easier to read

* **Note:** The one exception is with using the **ternary operator**

```Java
return (condition ? x : y);
```

# Package and import statements

* First **non-commented line** in the file should be a package statement, after **import statements** can follow

```Java
package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.persistence.hsqldb;

import import java.util.ArrayList;
```

# File Structure

* Android's guidelines can be found [here](https://developer.android.com/guide/topics/resources/providing-resources)

# The Gist

* Basically just Ctrl+Alt+L (or whatever you have formatting set to in Android Studio) before you commit your code.