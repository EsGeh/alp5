import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;


public class Client {

	public Client(Dictionary dictionary) {
		this.dictionary = dictionary;
		in = new Scanner(System.in);
		out = System.out;
	}

	/**
	 * @param args
	 */
	public void exec() {
		out.println("Hello to the distributed dictionary!");
		out.println("type an english word to find a german translation. ^D to exit"); 
		do {
			out.print("> ");
			if( ! in.hasNext())
				break;
			String index = in.nextLine();
			
			List<String> result = null;
			double deltaT = 0;
			try { 
				double t0 = System.nanoTime();
				result = dictionary.lookup(index);
				double t = System.nanoTime();
				deltaT = t - t0;
			}
			catch(RemoteException e) {
				out.println("exception while trying to lookup: " + e.getMessage());
				return;
			}
			
			if( result!=null ) {
				out.println("result: " + result + " (answer time: " + (float )deltaT / 1000 + "us)");
			}
			else {
				out.println("entry not found!");
			}
		}
		while( true /*in.hasNext()*/ );
	}
	
	protected Scanner in;
	protected PrintStream out;
	protected Dictionary dictionary;

}
