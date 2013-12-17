import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;


public abstract class ProtocolServer {

	public ProtocolServer(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public abstract Connection accept() throws IOException;
	
	public void exec() throws IOException {
		while(true) {
			Connection connection = accept();
			answer(connection);
		}
	}
	
	public void answer(Connection connection) {
		String word = connection.getIn().nextLine();
		try {
			List<String> result = dictionary.lookup(word);
			if( result!=null ) {
				String answer = "";
				for( String str : result ) {
					answer += ( str + " " );
				}
				answer = answer.substring(0, answer.length()-1 );
				
				connection.getOut().println(answer);
			}
			else {
				connection.getOut().println("");
			}
		}
		catch(RemoteException e) {
			System.out.println("remote exception: " + e.getMessage());
		}
	}

	private Dictionary dictionary;
	
	/*public class Service implements Runnable {
		
		public Service(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void run() {
			//
		}
		
		private Connection connection;
	}*/
}
