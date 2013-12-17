import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;


public class TCPServer extends ProtocolServer {

	public TCPServer(Dictionary dictionary, int port) throws IOException {
		super(dictionary);
		
		// create a server socket to listen for incoming connections:
		listen = new ServerSocket(port);
	}

	@Override
	public Connection accept() throws IOException {
		return new TCPConnection(listen.accept());
	}
	
	/*public void exec() {
		System.out.println("server has been started successfully");
		
		super.exec();
	}*/
	
	private ServerSocket listen;
	
	public void main(String []args) {
		
		// 0. parse command line arguments:
		if(args.length != 2) {
			System.out.println("syntax: TCPServer <dictFile> <port>");
			return;
		}
		
		String dictFile = args[0];
		int port = Integer.parseInt(args[1]);
		
		// 1. create a dictionary:
		DictionaryImpl dict = new DictionaryImpl();
		try {
			dict.init(dictFile);
		}
		catch(FileNotFoundException e) {
			System.out.println("exception while creating dictionary: " + e.getMessage());
		}
		
		// 2. create the server:
		TCPServer server = null;
		try {
			 server = new TCPServer(dict, port);
		}
		catch(IOException e) {
			System.out.println("exception while opening port: " + e.getMessage());
			return;
		}
		
		// 3. execute it:
		try {
			server.exec();
		}
		catch(IOException e) {
			System.out.println("exception caught: " + e.getMessage());
		}
	}	
}