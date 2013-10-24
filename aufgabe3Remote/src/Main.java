import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Main {
	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintStream out = System.out;
		
		EchoProcess echoProc = new EchoProcess(in, out);
		new Thread(echoProc).start();
	
		// ticker process sends the current time every seconds:
		try {
			for(int counter=0; counter<60; counter ++) {
				out.println(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
				out.flush();
				Thread.sleep(1000);
			}
		}
		catch(InterruptedException e) {
			out.println("InterruptedException: " + e.getMessage());
		}
		echoProc.stop();
	}
	volatile boolean running = true;
}
