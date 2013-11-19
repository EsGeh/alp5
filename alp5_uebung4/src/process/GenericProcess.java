package process;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public abstract class GenericProcess<M>
	extends KnowsPeers<M>
	implements Process<M> {
	
	public void start(Process<M>[] peers) {
		// register peers:
		super.start(peers);
		//run the thread:
		super.start();
	}

	@Override
	public void send(M message) {
		try {
			mailbox.put(message);
		}
		catch(InterruptedException e) {
			// no idea, what to do!
			log.log(Level.WARNING, "Exception caught: " + e.getMessage());
		}
	}
	
	protected M recv() {
		M ret = null;
		try {
			ret = mailbox.take();
		}
		catch(InterruptedException e) {
			// no idea, what to do!
			log.log(Level.WARNING, "Exception caught: " + e.getMessage());
		}
		return ret;
	}
	
	public GenericProcess(String name, int boxSize) {
		log = Logger.getLogger(this.getClass().getName() + "(" + name + ")" );
		//System.out.println(name + "(" + this.getClass().getName() + ")");
		try {
			FileHandler fh = new FileHandler("log",false);
			fh.setFormatter(new SimpleFormatter());
			fh.setLevel(Level.ALL);
			/*ConsoleHandler ch = new ConsoleHandler();
			ch.setFormatter(new SimpleFormatter());
			ch.setLevel(Level.ALL);*/
			log.addHandler(fh);
			//log.addHandler(ch);
			log.setLevel(Level.ALL);
		}
		catch(SecurityException | IOException e) {
			System.out.println("exception while creating log: " + e.getMessage());
		}
		
		mailbox = new ArrayBlockingQueue<M>(boxSize);
	}
	
	
	private BlockingQueue<M> mailbox;
	protected Logger log;
}
