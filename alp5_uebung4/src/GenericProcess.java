import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class GenericProcess<M>
	extends Thread
	implements Process<M> {

	@Override
	public void start(Process<M>[] peers) {
		this.peers = peers;
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
		log = Logger.getLogger(name + "(" + this.getClass().getName() + ")");
		
		mailbox = new ArrayBlockingQueue<M>(boxSize);
	}
	
	private Process<M> peers[];
	private BlockingQueue<M> mailbox;
	protected Logger log;
}
