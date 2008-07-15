/**
 * Copyright (C) 2008 J.F. Zarama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.zmatrix.cli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

 /**
 * Offers an API to parse commands to a console application
 *
 * @author jf.zarama@gmail.com
 */
public class ParseCmd {
    private final Map<String,Map<String,String>>  Parms;
    private final String                          help;
    private static final String pName    = "name";
    private static final String pValue   = "value";
    private static final String pRex     = "rex";
    private static final String pReq     = "req";
    private static final String pMsg     = "msg";
    private static final String nRegEx   = "^([+-]{0,1})([0-9.]{1,})$";
    private static final String sRegEx   =
                                  "^[^0-9]{1}([a-zA-Z0-9\\/\\_:\\.~]{1,})$";
    private static final String oddParms = "enter '-parmName  value' pairs";
    private static final String usage    =
              "usage: -loop n  -delay nnn -if fileName [ -tt nn  -of abc ]";

    /**
     *
     */
    public static class Builder {
        private final Map<String,Map<String,String>>  Parms;
        private       String                          help;
        private       Map<String,String>              entryMap;

        /**
         * Builder offers a way to build a Map, Parms, that can later be
         * used by the enclosing class, ParseCmd.
         *
         * Each entry in Parms has a name, default value, regular expression,
         * flag indicating whether it is required or it is optional and a
         * message String used to display help/error as needed.
         *
         */
        public Builder() {
            Parms = new LinkedHashMap<String,Map<String,String>>();
            help  = "";
        }

        private Builder parm(String name, String value) {// entry & defaults
            String ire = value.matches(nRegEx) ? nRegEx : sRegEx;//default regEx
            entryMap   = new LinkedHashMap<String,String>();// new Map for entry
            entryMap.put(pName,name);                   // parm name
            entryMap.put(pValue,value);                 // default value
            entryMap.put(pRex,ire);                     // default regEx
            entryMap.put(pReq,"0");                     // default not required
            entryMap.put(pMsg,"");                      // message if error
            Parms.put(name,entryMap);                   // add it to Parms Map
            return this;
        }

        /**
         *
         * @param h stores help/usage String to be used for usage information
         * @return  reference to this, Builder, so that chainning can be used
         */
        public Builder help(String h) {                 // define help String
            help = h;
            return this;
        }

        /**
         *
         * @param rex stores the regular-expression String for this arg
         * @return    reference to this, Builder, so that chainning can be used
         */
        public Builder rex(String rex) {                // define regExpression
            entryMap.put(pRex,rex);                     // default is set based
            return this;                                // on default value
        }

        /**
         *
         * @param req stores "0" or "1" String to flag whether it is required
         *            or optional; default is "0", optional
         * @return    reference to this, Builder, so that chainning can be used
         */
        public Builder req(String req) {                // required argument
            entryMap.put(pReq,req.matches("^[01]{1}$") ?// ensure "1" or "0"
                                             req : "0");// note: not boolean
            return this;                                // to keep it simple
        }

        /**
         *
         * @param desc stores error/help information to be displayed should
         *             regex fail to match
         * @return     reference to this, Builder, so that chainning can be used
         */
        public Builder msg(String desc) {              // define error message
            entryMap.put(pMsg,desc);                   // should parm fail
            return this;                                // rex test
        }

        /**
         *
         * @return instance of surrounding class passing Builder as argument
         */
        public ParseCmd build() {                       // return a CLI
            return new ParseCmd(this);                  // passing this Builder
        }
    }

    private ParseCmd(Builder builder) {                 // private constructor
        Parms = builder.Parms;                          // copy Parms Map
        help  = builder.help;                           // copy help String
    }

    /**
     *
     * @return defined help/usage String built by Builder
     */
    public String getHelp() {                           // return help String
        return this.help;
    }

    /**
     *
     * @param parmName defined parm-name as entered in Parms Map by Builder
     * @return         default value for parmName
     */
    public String getValue(String parmName) {           // get value for parm
        return getVars(parmName,pValue);
    }

    /**
     *
     * @param parmName  defined parm-name as enterd in Parms Map by Builder
     * @return          reg-expression String for parmName
     */
    public String getRex(String parmName) {             // get regEx for parm
        return getVars(parmName,pRex);
    }

    /**
     *
     * @param parmName  defined parm-name as enterd in Parms Map by Builder
     * @return          required flag, String "0" or "1" for parmName
     */
    public String getReq(String parmName) {             // get required
        return getVars(parmName,pReq);                  // "0" or "1"
    }

    /**
     *
     * @param parmName  defined parm-name as enterd in Parms Map by Builder
     * @return String value for error/help message for defined entry in Parms
     */
    public String getMsg(String parmName) {            // get emsg for parm
        return getVars(parmName,pMsg);                 // shown when rex fails
    }

    /**
     *
     * @return size of Parms Map indicating number of defined parms
     */
    public int size() {                                 // number of parms
        return this.Parms.size();                       // defined
    }

    /**
     * Parses the args[] array and merges its values with the default values
     * as recoded in the Parms Map.
     *
     * @param args   Array of input values
     * @return       Map merging args[] and default values in Parms Map
     */
    public Map<String,String> parse(String[] args) {   // merge args & defaults
        Map<String,String> R = new LinkedHashMap<String,String>();
        String k,v;
        for(int i=0;i<args.length;i +=2) {
            k = args[i];
            v = (i+1)<args.length ? args[i+1] : "";
            if(this.Parms.containsKey(k)) R.put(k, v);
        }
        for(Iterator p = this.Parms.keySet().iterator();p.hasNext();) {
            k = (String) p.next();
            v = getValue(k);
            if( !R.containsKey(k)) R.put(k, v);
        }
        return R;                                       // R parsed + defaults
    }

    /**
     * Validates args[] against regular expressions defined for each arg entry
     * in Parms Map
     *
     * @param  args
     * @return empty String if regex's pass or a message containing reason for
     *         failure. It includes also the help String as defined by Builder
     *         User should call validate() first; a non-emty String indicates
     *         parse error, and message is included. Should an empty String
     *         be the response, caller should invoke parse() which returns
     *         a merged Map using args[] and default settings in Parms
     */
    public String validate(String[] args) {             // validate args
        int np = args.length < 2 ? 1 : args.length % 2;
        if(np > 0) return "\n" + oddParms + "\n\n" + getHelp();
        String required = validateRequired(args);       // required args
        if(!required.isEmpty()) {                       // return if notEmpty
            return "\nenter required parms: " + required + "\n\n" + getHelp();
        }                                               // include getHelp()
        StringBuffer sb = new StringBuffer();           // checl each arg
        String k,v,re;
        for(int i=0;i < args.length-1;i +=2 ) {         // iterate over args
            k = args[i];                                // k <- arg
            v = args[i+1];                              // v <- value
            if(getValue(k).isEmpty()){sb.append("\n" + k + " invalid "); break; }
            re = getRex(k);                             // get regEx
            if(!v.matches(re)) {                        // if no match add to sb
                sb.append( "\n" + k + " value of  '" + v + "' is invalid;\n");
                sb.append( "\t" + getMsg(k));          // append arg emsg
                break;
            }
        }
        if(sb.length() > 0) sb.append("\n\n\t" + getHelp() + "\n");
        return sb.toString();                           // ok: if emty String
    }

    private List<String> findRequired() {               // List <- required args
        String k,req;
        List<String> R = new ArrayList<String>();
        for(Iterator p = Parms.keySet().iterator(); p.hasNext();) { // iterate
            k = (String) p.next();                      // over Parms.ketSet()
            req = getReq(k);                            // get req var
            if(req.equals("1")) R.add(k);               // add it to List if "1"
        }
        return R;                                       // List of req'd args
    }

    private String validateRequired(String[] args) {    // validate that all
        StringBuffer sb = new StringBuffer();           // required args
        List<String> R  = findRequired();               // are supplied
        int found = 0;
        for(String r : R) {
            for(String arg : args) if(r.equals(arg)) { found++; break; }
        }
        for(int i=0;i<R.size() && R.size() != found;i++) {  // where all req'd
            sb.append(R.get(i) + " ");                  // args found; if not
        }                                               // append req'd args
        return sb.toString().trim();                    // emty if all ok
    }                                                   // else show all req'd

    private String getVars(String parmName,String varName) {    // util to get
        Map<String,String> E;                                   // parm vars
        String r = "";                                          // such as:
        if(!this.Parms.containsKey(parmName)) return r;         //      value
        E = this.Parms.get(parmName);                           //      regex
        if(!E.containsKey(varName)) return r;                   //      req
        return E.get(varName);                                  //      emsg
    }

    private static String fill(String sep,int n){   // private util for repeated
        StringBuffer sb = new StringBuffer();       // characters n using
        for(int i=0;i<n;i++) sb.append(sep);        // a separator String
        return sb.toString();
    }

    // methods below are not neded by CLI but used for testing under
    // public static void main(String[] args)

    private String displayR(Map<String, String> R) {    // used for testing
        StringBuffer sb = new StringBuffer();           // display Map
        String name,k;                                  // such as result of
        name = R.get("name");                           // parse process
        if(name != null) sb.append(name + ":" + "\n");
        for(Iterator r = R.keySet().iterator();r.hasNext();) {
            k = (String) r.next();
            if(k.equals("name")) continue;
            sb.append(fill(" ",20) + k + fill(" ",20-k.length())
                                                     + R.get(k));
            sb.append("\n");
        }
        return sb.toString();
    }

    private  String displayP(Map<String,Map<String,String>> M) {// for test
        StringBuffer sb = new StringBuffer("\n");       // to display
        String k;                                       // Map of Maps such as
        Map<String,String> E;                           // the Parms Map
        for(Iterator m = M.keySet().iterator();m.hasNext();) {
            k = (String) m.next();
            E = M.get(k);
            sb.append(displayR(E)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Convenience method to generate a String representing the values, parms,
     * produced by Buider and store in Parms
     *
     * @return      String sutable for printing by caller
     */
    public String displayParms() {
        return displayP(this.Parms);
    }

    /**
     * Convenience method for the caller to print the Map produced by parse()
     * @param M     A Map collections variable typically the output of
     *              parse(args)
     * @return      String representation of Map suitable for printing
     */
    public String displayMap(Map<String, String> M) {
        return displayR(M);
    }

    /**
     * main is included to show usage of the class to parse commands
     * for a console application.
     *
     * Three steps are required to use the ParseCmd
     *
     *      1. Define       new ParseCmd.Builder(.help("usage .").parm()...
     *      2. Validate     String err = cmd.validate(args);
     *      3. Parse        if(err.isEmpty() R = cmd.parse(args);
     *
     * @param args input args from command line
     */
    public static void main(String[] args) {

        ParseCmd cmd = new ParseCmd.Builder()
                  .help(usage)
                  .parm("-loop",  "10" ).req("1")
                  .parm("-delay", "100").req("1")
                                        .rex("^[0-9]{3}$")
                                        .msg("must enter 3-digits.")
                  .parm("-if",    "./java.txt").req("1")
                  .parm("-tt",    "0")
                  .parm("-of",    "readme.txt")
                  .build();

        System.out.println(cmd.displayParms());
        String err = cmd.validate(args);
        Map<String,String> R;
        if( err.isEmpty()) {
            R = cmd.parse(args);
            System.out.println(cmd.displayMap(R));
        }
        else System.out.println(err);
    }
}
