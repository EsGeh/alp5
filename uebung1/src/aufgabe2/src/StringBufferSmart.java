

public class StringBufferSmart implements Buffer<String> {

	public void send(String string) {
		while( isFull ) { };
		
		write(string);
		
		if( isFull() ) { isFull=true; }
		isEmpty = false;
	}

	public String recv() {
		while( isEmpty ) { };
		
		String ret = read();
		
		if( isEmpty() ) { isEmpty=true; }
		isFull = false;
		
		return ret;
	}

	@Override
	public int length() {
		return count();
	}
	public StringBufferSmart(int maxSize) throws InvalidSizeException {
		if( maxSize<0 )
			throw new InvalidSizeException();
		
		this.maxSize = maxSize;
		buffer = new String[maxSize];
		
		this.isEmpty = true;
		if( maxSize > 0)
			this.isFull = false;
		
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
	
	/* precond:  ! isEmpty() */
	protected String read() {
		pr = (pr + 1) % maxSize;
		String ret = new String(buffer[pr]);
		buffer[pr] = null;
		return ret;
	}
	
	/* precond: ! isFull() */
	protected void write(String string) {
		buffer[pw] = new String(string);
		pw = (pw + 1) % maxSize;
	}
	
	private volatile boolean isFull;
	private volatile boolean isEmpty;
	private int maxSize;
	private String[] buffer;
	private int pw, pr; // write pointer, read pointer
	
	public class InvalidSizeException extends Exception {
	}
}
