import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Filter {
	enum Mode {
		OUTPUT_VALID,	// "+" : output words found in the dictionary
		OUTPUT_INVALID		// "-" : output words NOT found in the dictionary
	};
	
	/**
	 * @param args
	 * usage: Filter (+|-) language [-c host port] [-s]
	 */
	public static void main(String[] args) {
		Filter pThis = new Filter();
		try {
			pThis.readParams(args);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		
		try {
			pThis.init();
		}
		catch(IOException e) {
			System.out.println("exception while initializing: " + e.getMessage());
			return;
		}
		
		// check2 usage: check 
		pThis.exec();
	}
	
	public Filter() {
	}
	
	public void init() throws IOException {
		if( isServer ) {
			serverSocket = new ServerSocket(0);
			int port = serverSocket.getLocalPort();
			System.err.println(port); System.err.flush();
			//SocketAddress addr = new InetSocketAddress("localhost", port);
			//serverSocket.bind(addr);
			listen = serverSocket.accept();
			in = new Scanner(listen.getInputStream());
		}
		else {
			in = new Scanner(System.in);
		}
		
		if( isClient) {
			clientSocket = new Socket(host,port);
			out = new PrintStream(clientSocket.getOutputStream());
		}
		else {
			out = System.out;
		}
	}
	
	public void exec() {
		// syntax: check2 DICT (+|-)
		//String command = "ls";
		String command = "./dist/build/check2/check2 " + language + " " + ((mode==Mode.OUTPUT_VALID) ? "+" : "-");
		Process p = null;
		try {
			p = Fork.fork(command);
		} catch (IOException e) {
			System.out.println( "exception while executing \"" + command + "\" :\n" + e.getMessage() );
			return;
		}
		
		try {
			InToOut inToProg = new InToOut(in, new PrintStream(p.getOutputStream()));
			Thread inToProgThread = new Thread(inToProg);
			inToProgThread.start();
		
			InToOut progToOut = new InToOut(new Scanner(p.getInputStream()), out);
			InToOut progErrToOut = new InToOut(new Scanner(p.getErrorStream()), out);
			Thread progToOutThread = new Thread(progToOut);
			Thread progErrToOutThread = new Thread(progErrToOut);
			progToOutThread.start();
			progErrToOutThread.start();
			
			p.waitFor();
			//System.out.println("program exited");
			
			progToOutThread.join();
			progErrToOutThread.join();
			
			//p.waitFor();
		} catch(InterruptedException e) {
			System.out.println( "exception while redirecting channels" + e.getMessage() );
		}
	}

	// program arguments:
	private Mode mode;
	private String language;
	
	private boolean isClient;
	private String host;
	private int port;
	private boolean isServer;
	
	// input and output channels of the program:
	private Scanner in;
	private PrintStream out;
	
	// sockets are only used, if the corresponding program arguments have been used:
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private Socket listen;
	
	private void readParams(String[] args) throws Exception {
		try {
			if( !(args.length == 2 || args.length == 3 || args.length == 5 || args.length == 6)) {
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
			while( currentParam < args.length ) {
				if( args[currentParam].equals("-c")) {
					isClient = true;
					currentParam++;
					host = args[currentParam];
					currentParam++;
					port = Integer.parseInt( args[currentParam] );
					currentParam++;
				}
				else if( args[currentParam].equals("-s")) {
					isServer = true;
					currentParam++;
				}
			}
		}
		catch(Exception e) {
			throw new Exception(
				"usage: Filter (+|-) language [-c host port] [-s]\n" +
				"\t" + e.getMessage()
			);
		}
	}
}
