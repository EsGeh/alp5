import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        InputStream fromRemoteTemp = p.getInputStream();
        BufferedReader fromRemote = new BufferedReader(new InputStreamReader(fromRemoteTemp));
        PrintStream toRemote = new PrintStream(p.getOutputStream());
        
        BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            while(true) {
            	// user <- remoteProcess
            	if(fromRemote.ready()) {
            		String temp = fromRemote.readLine();
	                if(temp != null){
	                	String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	                    System.out.println(time + ": got \"" + temp + "\"");
	                }
	            // user -> remoteProcess
            	} else if( fromUser.ready()) {
	                String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            		String string = fromUser.readLine();
            		System.out.println(time + ": sending \"" + string + "\"");
            		toRemote.println(string);
            		toRemote.flush();
            	}
            	try {
            		Thread.sleep(50);
            	} catch(InterruptedException e) {
            		e.printStackTrace();
            	}
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
