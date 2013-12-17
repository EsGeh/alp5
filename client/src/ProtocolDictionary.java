import java.rmi.RemoteException;
import java.util.LinkedList;


public class ProtocolDictionary implements Dictionary {

	public ProtocolDictionary(Connection connection) {
		this.connection = connection;
		//answer = null;
	}

	@Override
	public LinkedList<String> lookup(String word) throws RemoteException {
		connection.getOut().println(word);
		
		String results = connection.getIn().nextLine();
		// fill return list:
		LinkedList<String> resultList = new LinkedList<String>();
		for( String result : results.split(" ")) {
			resultList.add(result);
		}
		return resultList;
	}
	
	//private String answer;
	private Connection connection;
}
