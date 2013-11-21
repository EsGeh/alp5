package udp;

public class SyntaxException extends Exception {
	
	public SyntaxException() {
	}

	public SyntaxException(String message) {
		super(message);
	}

	public SyntaxException(Throwable cause) {
		super(cause);
	}
	private static final long serialVersionUID = 1L;
}
