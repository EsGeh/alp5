
public class RMIClient extends Client {

	public RMIClient(Dictionary dict) {
		super(dict);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Dictionary dict = null;
		// to do: fetch dictionary using rmi:
		// ...
		
		RMIClient pThis = new RMIClient(dict);
		pThis.exec();
	}
	
}
