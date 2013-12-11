import java.io.IOException;


public class Filter {
	enum Mode {
		OUTPUT_VALID, // "+" : output words found in the dictionary
		OUTPUT_INVALID // "-" : output words NOT found in the dictionary
	};
	
	/**
	 * @param args
	 * usage: Filter (+|-) language [-c host port] [-s]
	 */
	public static void main(String[] args) {
		Filter pThis = new Filter();
		pThis.readParams(args);
		
		// check2 usage: check 
		pThis.exec();
	}
	
	public Filter() {
	}
	
	public void exec() {
		// syntax: check2 [host@server:]DICT
		String command = "check2 " + language + " " + ((mode==Mode.OUTPUT_VALID) ? "+" : "-");
		try {
			Process p = Fork.fork(command);
		} catch (IOException e) {
			System.out.println( "exception while executing \"" + command + "\" :\n" + e.getMessage() );
		}
	}
	
	private Mode mode;
	private String language;
	private boolean isClient;
	private String host;
	private int port;
	private boolean isServer;
	
	private void readParams(String[] args) {
		try {
			if( !(args.length == 2 || args.length == 5 || args.length == 6)) {
				throw new Exception("invalid syntax");
			}
			String plusMinus = args[0];
			if( !(plusMinus.equals("+") || plusMinus.equals("-")) ) {
				throw new Exception("invalid syntax");
			}
			mode = plusMinus.equals("+") ? Mode.OUTPUT_VALID : Mode.OUTPUT_INVALID;
			language = args[1];
			/*String language_ = args[1];
			if( !(language_.equals("en") || language_.equals("de")) ) {
				throw new Exception("language not supported \"" + language_ + "\"");
			}
			if( language_.equals("en") ) {
				language_ = "en";
			} else if( language_.equals("de") ) {
				language_ = "de";
			}*/
			if( args.length < 3) {
				return;
			}
			int currentParam = 2;
			if( args[currentParam].equals("-c")) {
				isClient = true;
				currentParam++;
				host = args[currentParam];
				currentParam++;
				port = Integer.parseInt( args[currentParam] );
				currentParam++;
			}
			if( args.length < 6 ) {
				return;
			}
			if( args[currentParam].equals("-s")) {
				isServer = true;
			}
		}
		catch(Exception e) {
			System.out.println("usage: Filter (+|-) language [-c host port] [-s]");
			System.out.println( "\t" + e.getMessage() );
		}
	}
}
