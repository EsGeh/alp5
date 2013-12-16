import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;


public class Client {

	public Client(Dictionary dict) {
		this.dict = dict;
		in = new Scanner(System.in);
		out = System.out;
	}

	/**
	 * @param args
	 */
	public void exec() {
		while( in.hasNext() ) {
			String index = in.nextLine();
			
			List<String> result = null;
			try {
				result = dict.lookup(index);
			}
			catch(RemoteException e) {
				out.println("exception while trying to lookup: " + e.getMessage());
				return;
			}
			
			if( result!=null ) {
				out.println("result: " + result);
			}
			else {
				out.println("entry not found!");
			}
		}
	}
	
	private Scanner in;
	private PrintStream out;
	private Dictionary dict;

}
