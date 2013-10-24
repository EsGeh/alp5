import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: andre
 * Date: 10/24/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static void main(String[] args){
        Process p = null;
        try {
            p = Fork.fork(args[0]);
            //p = Fork.fork("andreroehrig@wuhan.imp.fu-berlin.de:ls");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream in = p.getInputStream();
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));
        try {
            String temp = null;
            do {
                temp = bin.readLine();
                if(temp != null){
                    System.out.println(temp);
                }
            } while (temp != null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
