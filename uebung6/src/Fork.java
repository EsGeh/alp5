import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: andre
 * Date: 10/24/13
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Fork {
    public static Process fork(String input) throws IOException {
        Process p = null;
        if (pThis == null){
            pThis = new Fork();
        }
        if (!pThis.isremote(input)) {
            p = Runtime.getRuntime().exec(input);
        }
        else if(pThis.isremote(input)) {
            p = Runtime.getRuntime().exec("ssh "+input.replace(":"," "));
        }
        return p;
    }

    public boolean isremote(String input){
        if(input.split(" ")[0].contains(":")){
            return true;
        }
        else{
            return false;
        }
    }

    static Fork pThis;
}
