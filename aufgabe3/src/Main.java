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
            p = Fork.fork("pwd");
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream in = p.getInputStream();
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));
        try {
            System.out.println(bin.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
