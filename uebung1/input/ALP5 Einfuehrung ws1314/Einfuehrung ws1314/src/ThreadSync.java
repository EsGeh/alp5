import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;


public class ThreadSync {
	static Logger logger = Logger.getLogger(ThreadSync.class.getName());
	
	int counter = 0;
	
	public static void main(String[] args) {
		new ThreadSync();
	}

	public ThreadSync() {
		logger.info("starting test...");
		
		Printer pa = new Printer() {
			@Override
			public void print() {
				synchronized (ThreadSync.this) {
					while (counter % 3 != 0) {
						try {
							ThreadSync.this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					counter++;
					System.out.println('A');
					ThreadSync.this.notifyAll();
				}
			}
		};
		Printer pb = new Printer() {
			@Override
			public void print() {
				synchronized (ThreadSync.this) {
					while (counter % 3 != 1) {
						try {
							ThreadSync.this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					counter++;
					System.out.println('B');
					ThreadSync.this.notifyAll();
				}
			}
		};
		Printer pc = new Printer() {
			@Override
			public void print() {
				synchronized (ThreadSync.this) {
					while (counter % 3 != 2) {
						try {
							ThreadSync.this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					counter++;
					System.out.println('C');
					ThreadSync.this.notifyAll();
				}
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
