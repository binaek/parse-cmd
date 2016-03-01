# Requirement #
There are several, hundreds and possibly more, command line parser programs for Java.

A quick search on Google has several entries; the top results include the following products.

  * [argparser](http://www.cs.ubc.ca/~lloyd/java/argparser.html) It is the first to appear in the list of results from Google to the search: 'cli parser java'
  * [JArgs](http://jargs.sourceforge.net) Last updated 2005.April.12
  * [parseCmdLine](http://journals.ecs.soton.ac.uk/java/tutorial/java/cmdLineArgs/parsing.html) Java tutorial available at a UK Java site.
  * [JSAP](http://www.martiansoftware.com/jsap/) Java Simple Argument Parser.
  * [JOpt Simple](http://jopt-simple.sourceforge.net/) JOpt Simple is a Java library for parsing command line options, such as those you might pass to an invocation of javac.
  * [cli-parser](http://code.google.com/p/cli-parser/) Annotations based Solution hosted on Google Code.
  * [jacli](http://code.google.com/p/jacli/) Another annotations based solution hosted on Google Code.
  * [JewelCli](http://jewelcli.sourceforge.net/) JewelCli uses an annotated interface definition to automatically parse and present command line arguments.
  * [Apache Commons](http://commons.apache.org/cli/) The Apache Commons CLI library provides an API for processing command line interfaces.
  * [a blog entry re subject](http://javathink.blogspot.com/2008/04/why-does-parsing-arguments-in-java-suck.html) This blog describes well the state of command-line software for java and lists 13-parser-products; its heading summarizes well the current situation: "Why does parsing arguments in Java SUCK so much?"

Reviewing these and other entries, it was time to try another parser. The requirements are as follows:
  1. **Definition.** It should allow a simple definition of each parameter entry including the following attributes:
    * **Name.** A primary name and alias to identify an entry.
    * **Value.** Default value. This value allows also to infer validation rule should one be unavailable.
    * **Validation expression.** Regular-expressions, regex, are identified as an appropriate solution to allow definition of various types, such as integer, date, time, etc., but without getting into Java types, using instead Strings + Regular Expressions. Regular Expressions allow also definition of value range(s).
    * **Help message.** Define a message string to describe the purpose and format of a parameter.
    * **Required flag.** Ability to indicate that a parameter is required; default is optional.
  1. **Parsing.** The process sees a **validate** method followed by a **parse** method producing a Map containing the resultant merged values for use by the caller.

The overall property, possibly at the expense of function, is to keep the solution simple.  Other properties include the following:
  * **Avoid use of Exceptions** A simple String response, empty if all is fine or containing a set of error messages. Typically but not always, the parse process is for interactive use, e.g an end-user rather than an automated process. Hence, a String suitable for viewing is desirable over exceptions. Therefore, a validate(args) method, returning and empty String or set of error messages, is preferred as the vehicle to respond to the caller.
  * **Result** A Map containing merged values, the parsed values plus defaults, is identified as the result object for the parse process. Three steps are apparent:
    1. **Define.** Define the parameters, name, default value, regular-expression(s), message, required flag.
    1. **Validate.** Method, String validate(args), used repeatedly until an empty String is obtained signaling a correctly parsed command sequence.
    1. **Parse.** Map parse(args) e.g obtain a Map containing all parameter names and associated parsed/default values.

Usage:
```
    public static void main(String[] args) {
        
        String usage = "usage: -loop n  -delay nnn -ifile fileName [ -tt nn  -ofile abc ]";
        ParseCmd cmd = new ParseCmd.Builder()           
                      .help(usage)                          
                      .parm("-loop",    "10" ).req()       
                      .parm("-delay",   "100").req()       
                                              .rex("^[0-9]{3}$") 
                                              .msg("must enter 3-digits.") 
                      .parm("-ifile",   "java.txt").req()
                      .parm("-tt",      "0")                  
                      .parm("-ofile",   "readme.txt")
                      .parm("-verbose", "0").rex("^[01]{1}$")
                      .build();  
       
        Map<String,String> R = new HashMap<String,String>();
        String parseError    = cmd.validate(args);
        if( cmd.isValid(args) ) {
            R = cmd.parse(args);
            System.out.println(cmd.displayMap(R));
        }
        else { System.out.println(parseError); System.exit(1); }                    
    }
```