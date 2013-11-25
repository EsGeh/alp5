import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger log = Logger.getLogger("server log");
		ServerSocket socket = null;
		try {
			socket = new ServerSocket();
			SocketAddress addr = new InetSocketAddress("localhost", 8000);
			socket.bind(addr);
			log.info("socket created");
		}
		catch(IOException e) {
			log.log(Level.SEVERE, "exception while creating socket: " + e.getMessage());
		}
		while( true ) {
			try {
				log.info("waiting for connections...");
				Socket clientSocket = socket.accept();
				new Thread(new Connection( clientSocket, log)).start();
			}
			catch(IOException e) {
				log.log(Level.SEVERE, "exception while calling Socket.accept " + e.getMessage());
			}
		}
	}
}