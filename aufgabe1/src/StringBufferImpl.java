

public class StringBufferImpl implements Buffer<String> {

	public synchronized void send(String string) {
		try {
			while( isFull()) {
				this.wait();
			}
			write(string);
			this.notifyAll();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized String recv() {
		try {
			String ret = null;
			while( isEmpty()) {
				this.wait();
			}
			ret = read();
			this.notifyAll();
			return ret;
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public synchronized int length() {
		return ;
	}
	public StringBufferImpl(int maxSize) throws InvalidSizeException {
		if( maxSize<0 )
			throw new InvalidSizeException();
		
		this.maxSize = maxSize;
		buffer = new String[maxSize];
		
		pr = pw = 0;
	}
	
	protected boolean isFull() {
		return pr == pw 
				&& buffer[pw] != null;
	}
	
	protected boolean isEmpty() {
		return pr == pw && buffer[pw] == null;
	}
	
	protected int count() {
		return pw - pr;
	}
	
	/* precond:  !isEmpty() */
	protected String read() {
		String ret = new String(buffer[pr]);
		buffer[pr] = null;
		pr = (pr + 1) % maxSize;
		return ret;
	}
	
	/* precond: !isFull() */
	protected void write(String string) {
		buffer[pw] = new String(string);
		pw = (pw + 1) % maxSize;
	}
	
	private int maxSize;
	private String[] buffer;
	private int pw, pr; // write pointer, read pointer
	
	public class InvalidSizeException extends Exception {
	}
}
