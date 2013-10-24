
public abstract class UsesBuffer implements Runnable {
	public UsesBuffer(Buffer<String> buffer) {
		this.buffer = buffer;
		this.running = true;
	}
	public void run() {
		//System.out.println("run called!");
		while(running) {
			useBuffer();
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				System.out.println("InterruptedException: " + e.getMessage());
			}
		}
	}
	
	public void stop() {
		running = false;
	}
	
	abstract void useBuffer() ;
	private boolean running;
	protected Buffer<String> buffer;
}
