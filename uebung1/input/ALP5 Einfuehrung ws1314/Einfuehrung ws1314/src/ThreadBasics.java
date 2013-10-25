import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;


public class ThreadBasics {
	static Logger logger = Logger.getLogger(ThreadBasics.class.getName());
	
	public static void main(String[] args) {
		new ThreadBasics();
	}

	public ThreadBasics() {
		logger.info("starting test...");
		
		Printer pa = new PrinterA();
		Printer pb = new PrinterB();
		Printer pc = new Printer() {
			@Override
			public void print() {
				System.out.println('C');
			}
		};
		
		new Thread(pa).start();
		new Thread(pb).start();
		new Thread(pc).start();
		
		this.killPrintersOnInput(pa, pb, pc);
	}
	
	public void killPrintersOnInput(Printer ... printers) {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    try {
			bufferRead.readLine();
		} catch (IOException e) {
			/* DAS IST NICHT SCH…N! ABER BESSER ALS ALLE ALTERNATIVEN! */
			throw new RuntimeException(e);
		}
		
		/* wenn wir hier ankommen, hat der Nutzer was in die Konsole eingegeben */
		for (Printer p : printers) {
			p.terminate();
		}
	}
}
