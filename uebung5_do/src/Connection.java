import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Connection implements Runnable {
	public void run() {
		Scanner inScanner = null;
		PrintStream out = null;
		log.info("creating streams for the connection...");
		try {
			inScanner = new Scanner(socket.getInputStream());
			out = new PrintStream(socket.getOutputStream());
		}
		catch(IOException e) {
			log.log(Level.SEVERE, "exception while creating streams for connection: " + e.getMessage());
		}
		out.println("do server\nAuthors: André Röhrig, Samuel Gfrörer");
		//out.flush();
		try {
			socket.close();
		}
		catch(IOException e) {
			log.log(Level.SEVERE, "exception while closing connection to client: " + e.getMessage());
		}
	}
	public Connection(Socket socket, Logger serverLog) {
		this.socket = socket;
		this.log = serverLog;
	}
	
	private Socket socket;
	private Logger log;
}
