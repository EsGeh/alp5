import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class RMIServer /*extends UnicastRemoteObject*/ {

	public RMIServer() /*throws RemoteException*/ {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 0. parse command line arguments:
		if(args.length != 2) {
			System.out.println("syntax: RMIServer <dictFile> <port>");
			return;
		}
		
		String dictFile = args[0];
		int port = Integer.parseInt(args[1]);
		
		// 1. create a security Manager:
		if( System.getSecurityManager() == null ) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			// 2. create a dictionary:
			DictionaryImpl dict = new DictionaryImpl();
			dict.init(dictFile);
			// 3. "install" it:
			Dictionary stub = (Dictionary )UnicastRemoteObject.exportObject(dict, port);
			
			// 4. start the rmi server:
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("dict", stub);
			
			System.out.println("server has been started successfully");
		}
		catch(FileNotFoundException | RemoteException e) {
			System.out.println("exception while installing dictionary: " + e.getMessage());
		}
	}

}