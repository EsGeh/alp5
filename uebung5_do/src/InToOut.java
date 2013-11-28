import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class InToOut implements Runnable {
	public InToOut(Scanner in, PrintStream out) {
		this.in = in;
		this.out = out;
		this.goOn = true;
	}
	
	public void terminate() {
		goOn = false;
		System.out.println("terminate called!");
		
		//Thread.currentThread().interrupt();
	}
	
	public void maybeThrow () throws IOException {
		if( exception != null)
			throw exception;
	}

	public void run() {
		String currentLine = null;
		try {
			while( goOn ) {
				if( in.hasNext() ) {
					System.out.println("has");
					currentLine = in.nextLine();
					out.println( currentLine );
					out.flush();
				}
				Thread.sleep(500);
			}
			System.out.println("InToOut terminated!");
			System.out.flush();
		}
		catch(Exception e) {
			System.out.println("InToOut interrupted");
			System.out.flush();
		}
	}
	private Scanner in;
	private PrintStream out;
	private IOException exception;
	private volatile boolean goOn;
		
};