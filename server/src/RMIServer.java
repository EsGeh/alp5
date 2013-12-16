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
		// 1. create a security Manager:
		if( System.getSecurityManager() == null ) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			// 2. create a dictionary:
			Dictionary dict = new DictionaryImpl();
			// 3. "install" it:
			Dictionary stub = (Dictionary )UnicastRemoteObject.exportObject(dict, 0);
			
			// 4. start the rmi server:
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("dict", stub);
			
			System.out.println("server has been started successfully");
		}
		catch(RemoteException e) {
			System.out.println("exception while installing dictionary: " + e.getMessage());
		}
	}

}