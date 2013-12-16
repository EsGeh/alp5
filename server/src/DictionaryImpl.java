import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
//import java.util.Set;


public class DictionaryImpl
	extends HashMap<String, LinkedList<String>>
	implements Dictionary { // (just to mention)

	/*public DictionaryImpl() {
	}*/

	@Override
	public LinkedList<String> lookup(String word) throws RemoteException {
		return this.get(word);
	}

}
