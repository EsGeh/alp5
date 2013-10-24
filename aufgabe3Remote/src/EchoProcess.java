import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;


public class EchoProcess implements Runnable {
	public void run() {
		try {
			while( running ) {
				if( in.ready()) {
					String currentInput = in.readLine();
					out.println("received \"" + currentInput + "\"");
					out.flush();
				}
				Thread.sleep(50);
			}
		}
		catch(IOException e) {
			out.println("IOException: " + e.getMessage());
			out.flush();
		}
		catch(InterruptedException e) {
			out.println("InterruptedException: " + e.getMessage());
			out.flush();
		}
	}
	
	public EchoProcess(BufferedReader in, PrintStream out) {
		this.in = in;
		this.out = out;
		running = true;
	}
	public void stop() {
		running = false;
	}
	
	private BufferedReader in;
	private PrintStream out;
	
	private boolean running;
}
