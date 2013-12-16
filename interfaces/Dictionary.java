import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;


// English-German dictionary
public interface Dictionary extends Remote {
	
	// get list of German translations for given English 'word'
	// or null if no such word	
	LinkedList<String> lookup(String word) throws RemoteException;
}