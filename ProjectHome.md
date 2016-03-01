# parse-cmd #

`ParseCmd.java` is a Java-class used to define and parse command-line parameters in a Java application.

See [ParseParms.scala](http://code.google.com/p/parse-cmd/wiki/AScalaParserClass) for an equivalent implementation using [Scala](http://www.scala-lang.org/)

It facilitates the definition of parameters, each including the following values:
  * name
  * default value
  * regular-expression used for validation
  * error/help information
  * flag to indicate whether the named parameter is required; defaults to optional

Here is a sample statement defining a required parameter, req(), named "-ifile", with a default value of "input.txt", and including a message should validation fail, msg(" ... ")
```
.parm("-ifile", "input.txt").req().msg("enter a valid file name")
```

## Why ##

Several Java-based command-line parsing solutions are available, such as http://commons.apache.org/cli, but they are generally complex to learn and use.

A set of [requirements](http://code.google.com/p/parse-cmd/wiki/Requirement) are listed and used to guide the development of yet another command-line parser for Java.

## Yet Another Parser ##

`ParseCmd.java` uses the [Builder pattern](http://en.wikipedia.org/wiki/Builder_pattern) to facilitate definition of each parameter, define or infer a [regular expression](http://en.wikipedia.org/wiki/Regular_expressions) based on the defined default value and a flag to mark the parameter as required or left optional by default.

It forces key-value pairs such as '--parmName  value' to define each parameter entry; the parameter value is used as a default for both value and inferred regular-expression used during validation. Also, a specific regular-expression may be defined for each parameter.

Parameters such as -verbose, are treated internally as a name-value pair, as it were entered -verbose 1. Such parameters may be defined as a name without value, or as a name-value pair as shown here:

```
.parm("-verbose")

or

.parm("-verbose", "0")

or

.parm("-verbose", "0").rex("^[01]{1}$")

or

.parm("-verbose", "0").rex("^[01]{1}$").msg("enter 0 or 1; other values are invalid")
```

The last statement defines an optional parameter named "-verbose" with a default value of "0" followed by a regular expression that limits input to one character of value "0" or "1", and an explanatory message used should the test fail using the supplied [regular expression](http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html), `"^[01]{1}$"`

## Example ##

Use of the class involves three steps:

  1. **Define.** Define the parameters.
  1. **Validate.** Call the validate(args) method; an empty String signals successful validation; alternatively the String contains one or more error messages,
  1. **Parse.** Should an empty String result from calling the validate(args) method, Map parse(args) returns a Map with keys as the parmNames and corresponding parsed/default values.

The file `parsecmd-0.0.93.jar` is available under **Downloads** and can be used to compile sample applications; it can also run the code included below as follows:

```
java  -jar  parsecmd-0.0.93.jar
java  -jar  parsecmd-0.0.93.jar   -h 
java  -jar  parsecmd-0.0.93.jar   -loop 1 
java  -jar  parsecmd-0.0.93.jar   -loop 1 -delay 2    -ifile  input.txt 
java  -jar  parsecmd-0.0.93.jar   -loop 1 -delay 333  -ifile  input.txt 
```

See the included example [here](http://code.google.com/p/parse-cmd/wiki/sampleSource).

jf.zarama at gmail dot com
