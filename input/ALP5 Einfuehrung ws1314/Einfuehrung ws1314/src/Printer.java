import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class Printer implements Runnable {
	static Logger logger = Logger.getLogger(Printer.class.getName());
	
	int numberOfPrints = 0;
	/** whether to terminate */
	boolean terminate = false;
	
	/** terminates the printer */
	public void terminate() {
		this.terminate = true;
	}
	
	/** print! 
	 * 
	 * 
	 */
	public abstract void print();
	
	@Override
	public void run() {
		while (!terminate) {
			print();
			try {
				/* wait one second */
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
			}
		}
		/* log termination */
		logger.info("terminated: " + this.getClass().getName());
	}
}
