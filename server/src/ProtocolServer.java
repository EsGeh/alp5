import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;


public abstract class ProtocolServer {

	public ProtocolServer(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public abstract Connection accept() throws IOException;
	
	public void exec() throws IOException {
		while(true) {
			Connection connection = accept();
			while(connection.isOpen())
				answer(connection);
		}
	}
	
	public void answer(Connection connection) {
		
		String word = null;
		try {
			word = connection.getIn().nextLine();
		}
		catch(NoSuchElementException e) {
			// connection dropped by the client:
			System.out.println("client dropped the connection!");
			try {
				connection.close();
			}
			catch(IOException e2) {
				System.out.println("exception while closing the connection: " + e2.getMessage());
			}
			return;
		}
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
		/*String goOn = connection.getIn().nextLine();
		if( goOn.equals("true"))
			return true;
		else
			return false;*/
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
