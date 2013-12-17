import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class TCPConnection implements Connection {

	public TCPConnection(Socket socket) throws IOException {
		this.socket = socket;
		in = new Scanner( socket.getInputStream() );
		out = new PrintStream( socket.getOutputStream() );
	}
	
	public Scanner getIn() {
		return in;
	}
	
	public PrintStream getOut() {
		return out;
	}

	/*@Override
	public String recv() {
		return in.nextLine();
	}

	@Override
	public void send(String message) {
		out.println(message);
	}*/
	
	private Scanner in;
	private PrintStream out;
	private Socket socket;
}
