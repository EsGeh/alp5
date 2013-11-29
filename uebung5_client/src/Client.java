import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;


public class Client {

	public Client() {
		socket = new Socket();
		userIn = new Scanner(System.in);
		userOut = System.out;
	}
	
	public void exec() {
		connectUI();
		
		dialog();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client pThis = new Client();
		
		pThis.exec();
		
		try {
			pThis.exit();
		}
		catch(IOException e) {
			System.out.println("error while calling exit: " + e.getMessage());
		}
	}
	
	void exit() throws IOException {
		userIn.close();
		in.close();
		out.close();
		
		disconnect();
	}
	
	void dialog() {
		System.out.print("server name: ");
		System.out.println(in.nextLine());
		System.out.print("authors: ");
		System.out.println(in.nextLine());
		
		
		String app = ""; String filename = "";
		File file = null;
		while(app.equals("")) {
			System.out.println("enter command to execute (syntax: <app> [<file>]):");
			System.out.print("  ");
			
			String command = userIn.nextLine();
			String[] temp = command.split(" ");
			if(temp.length == 1 || temp.length == 2) {
				app = temp[0];
				if( temp.length == 2) {
					filename = temp[1];
					file = new File(filename);
					if( !file.exists() || file.isDirectory() ) {
						app = ""; filename = "";
						file = null;
						System.out.println("file " + filename + " doesn't exist!");
					}
				}
			}
			else {
				System.out.println("syntax error");
			}
		}
		out.print(app);
		if( file != null ) { // syntax: <app> <file>
			try {
				out.println(" " + filename);
				out.println(file.length());
				Scanner fileIn = new Scanner(new FileInputStream(file));
				while( fileIn.hasNext() ) {
					out.println(fileIn.nextLine());
				}
				out.flush();
				
				fileIn.close();
			}
			catch(FileNotFoundException e) {
				System.out.println("fatal error");
			}
		}
		else { // syntax: <app>
			out.print("\n");
		}
		// receive ok:
		String fromServer = in.nextLine();
		if( ! fromServer.equals("ok") )
		{
			System.out.println("protocol error!");
		}
		
		//InToOut userToServer = new InToOut(userIn, out);
		InToOut serverToUser = new InToOut(in, userOut);
		new Thread(serverToUser).start();
		
		String fromUser ="";
		while( ! (fromUser = userIn.nextLine()).equals(":q")) {
			out.println(fromUser);
		}
		out.println(":q");
		out.flush();
		serverToUser.stop();
		
		//readFromFile(file);
	}
	
	void readFromFile(String filename) {
	}
	
	void connectUI() {
		String serverIP;
		
		System.out.println("please enter the server to connect to: ");
		System.out.print("ip or hostname: ");
		//in = new Scanner(System.in);
		serverIP = userIn.nextLine();
		
		connect(serverIP, port);
	}
	
	void connect(String serverIP, int port) {
		try {
			socket.bind(null);
		}
		catch(IOException e) {
			System.out.println("unable to bind socket: " + e.getMessage());
			return;
		}
		
		SocketAddress serverAddr = new InetSocketAddress(serverIP, port);
		try {
			socket.connect(serverAddr);
		}
		catch(IOException e) {
			System.out.println("exception while connecting: " + e.getMessage());
			return;
		}
		// create streams:
		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintStream(socket.getOutputStream());
		}
		catch(IOException e) {
			System.out.println("could not create streams: " + e.getMessage());
		}
	}
	
	void disconnect() {
		try {
			socket.close();
		}
		catch(IOException e) {
			System.out.println("exception while closing socket: " + e.getMessage());
		}
	}
	
	public static final int port = 8000;
	
	private Socket socket;
	private Scanner userIn;
	private PrintStream userOut;
	private Scanner in = null;
	private PrintStream out = null;
	
	
	public class InToOut implements Runnable {
		public InToOut(Scanner in, PrintStream out) {
			this.in = in;
			this.out = out;
			this.run = true;
		}
		
		public void stop() {
			run = false;
		}
		
		public void maybeThrow () throws IOException {
			if( exception != null)
				throw exception;
		}
	
		public void run() {
			String currentLine = null; 
			try {
				while( run ) {
					currentLine = in.nextLine();
					//System.out.println("rec to redir");
					out.println( currentLine );
					out.flush();
				}
			}
			catch(Exception e) {
				
			}
		}
		private Scanner in;
		private PrintStream out;
		private IOException exception;
		private boolean run;
			
	};
}