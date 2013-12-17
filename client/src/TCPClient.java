import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class TCPClient extends Client {

	public TCPClient(Dictionary dict) {
		super(dict);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 0. read command line arguments:
		if( args.length != 2) {
			System.out.println("syntax: RMIClient <host> <port>");
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		// 1. connect to the server:
		Connection connection = null;
		try {
			connection = connect(host, port); 
		}
		catch( IOException e) {
			System.out.println("exception while connecting to server: " + e.getMessage());
			return;
		}
		
		// 2. create a protocol:
		Dictionary dict = new ProtocolDictionary(connection);
		
		// 3. create the client:
		TCPClient pThis = new TCPClient(dict);
		
		// 4. execute the client:
		pThis.exec();
		
		//connection
		try {
			connection.close();
		}
		catch(IOException e) {
			pThis.out.println("error while closing connection: " + e.getMessage());
			return;
		}
	}
	
	public static Connection connect(String host, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(host, port);
		return new TCPConnection(socket);
	}
	
	/*public void fetchDictionary(int port) throws RemoteException, NotBoundException {
		// 1. create a security Manager:
		if( System.getSecurityManager() == null ) {
			System.setSecurityManager(new SecurityManager());
		}
		// 2. fetch dictionary:
		Registry registry = LocateRegistry.getRegistry();
		dictionary = (Dictionary )registry.lookup("dict");
	}*/
}
