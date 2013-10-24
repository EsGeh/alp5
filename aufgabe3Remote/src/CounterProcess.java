import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CounterProcess implements Runnable{

	public void run() {
		try {
			for(int counter=0; counter<60; counter ++) {
				while( running ) {
					out.println(new SimpleDateFormat().format(Calendar.getInstance().getTime()));
					out.flush();
					Thread.sleep(1000);
				}
			}
		}
		catch(InterruptedException e) {
			out.println("InterruptedException: " + e.getMessage());
		}
	}
	
	public CounterProcess(PrintStream out) {
		running = true;
		this.out = out;
	}
	public void stop() {
		running = false;
	}
	
	private boolean running;
	private PrintStream out;
}
