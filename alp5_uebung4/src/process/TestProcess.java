package process;

import java.util.List;
import java.util.ArrayList;

public class TestProcess {
	public TestProcess() {
		List<EchoProcess> procs = new ArrayList<EchoProcess>(); //[countProcess];
		for( int i=0; i<countProcess; i++) {
			EchoProcess proc = new TestProcess.EchoProcess(Integer.toString(i));
			procs.add(proc);
		}
		for( int i=0; i<countProcess; i++) {
			EchoProcess proc = procs.get(i);
			EchoProcess next = procs.get( (i+1) % countProcess );
			proc.start(
					new Process[] {
						next
					}
				);
		}
		/*
		for( EchoProcess proc : procs ) {
			proc.run();
		}
		*/
		
		/*flood the ring with the message "exit"
		 * this causes all processes to terminate
		 */
		EchoProcess proc = procs.get(0);
		proc.send("exit");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestProcess pThis = new TestProcess();
	}
	
	public class EchoProcess extends GenericProcess<String> {
		public EchoProcess(String name) { 
			super(name,10);
		}
		public void run() {
			System.out.println("thread started!");
			boolean goOnRunning = true;
			do {
				String msg = recv();
				switch(msg) {
					case "exit":
					{
						log.info("received: " + msg);
						goOnRunning = false;
						// redirect message to every peer:
						for( Process<String> p : getPeers()) {
							p.send(msg);
						}
					}
					break;
					default:
					{
						log.info("received: " + msg);
						// redirect message to every peer:
						for( Process<String> p : getPeers()) {
							p.send(msg);
						}
					}
					break;
				}
			} while (goOnRunning);
		}
	};
	
	public static final int countProcess = 5 ;
}
