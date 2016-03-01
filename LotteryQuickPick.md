# lottery quick-pick number generator #

```

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import ca.zmatrix.cli.ParseCmd;

class ZRandom {

    static Random R = new Random(System.currentTimeMillis() * 173L);

    public static void main(String[] args) {

        ParseCmd cmd = new ParseCmd.Builder()
                      .help("[ -l l649 | s7 ] [-r nn] ")
                      .parm("-l","l649" )
                      .parm("-r","1")
                      .build();

        Map<String,String> R = new HashMap<String,String>();
        String parseError    = cmd.validate(args);
        if( cmd.isValid(args) ) {
            R = cmd.parse(args);
            System.out.println(cmd.displayMap(R));
        } else {
            System.out.println( parseError );
            System.exit(1);
        }

        int n = R.get("-l").toString().equals("l649") ?  6 :  7;
        int m = R.get("-l").toString().equals("l649") ? 49 : 47;
        int r = Integer.parseInt(R.get("-r").toString());


        List<Integer> X = new ArrayList<Integer>();
        for(int j=0;j<r;j++) {
            for(int i=0;i<n;i++) {
                Integer K = new Integer(nextRandom(m));
                while(X.indexOf(K) > -1) K = new Integer(nextRandom(m));
                X.add(K);
            }
            java.util.Collections.sort(X);
            System.out.println(X);
            X.clear();
        }

    }

    private static int nextRandom(int k) {
        return 1 + R.nextInt(k);
    }
}

```