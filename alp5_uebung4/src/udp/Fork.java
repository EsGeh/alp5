package udp;

import java.io.IOException;

public class Fork {
	public static Process fork(String command) throws IOException {
		Process p = null;
        if (pThis == null) {
            pThis = new Fork();
        }
        if (!pThis.isremote(command)) {
            p = Runtime.getRuntime().exec(command);
        }
        else if(pThis.isremote(command)){
            p = Runtime.getRuntime().exec("ssh " + command.replace(":"," "));
        }
        return p;
    }

    public boolean isremote(String input) {
        if(input.contains(":")) {
            return true;
        }
        else{
            return false;
        }
    }

    static Fork pThis;
}
