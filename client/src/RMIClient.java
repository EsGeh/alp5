import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class RMIClient extends Client {

	public RMIClient(Dictionary dict) {
		super(dict);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Dictionary dict = null;
		RMIClient pThis = new RMIClient(dict);
		
		// 0. read command line arguments:
		if( args.length != 1) {
			pThis.out.println("syntax: RMIClient <port>");
		}
		int port = Integer.parseInt(args[0]);
		
		// 1. fetchDictionary:
		try {
			pThis.fetchDictionary(port);
		}
		catch(NotBoundException | RemoteException e) {
			pThis.out.println("exception while fetching dictionary: " + e.getMessage());
		}
		
		// 2. execute the client:
		pThis.exec();
		
	}
	
	public void fetchDictionary(int port) throws RemoteException, NotBoundException {
		// 1. create a security Manager:
		if( System.getSecurityManager() == null ) {
			System.setSecurityManager(new SecurityManager());
		}
		// 2. fetch dictionary:
		Registry registry = LocateRegistry.getRegistry();
		dictionary = (Dictionary )registry.lookup("dict");
	}
}
