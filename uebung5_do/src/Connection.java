import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Connection implements Runnable {
	public void run() {
		in = null;
		out = null;
		log.info("creating streams for the connection...");
		try {
			in = new Scanner(socket.getInputStream());
			out = new PrintStream(socket.getOutputStream());
		}
		catch(IOException e) {
			log.log(Level.SEVERE, "exception while creating streams for connection: " + e.getMessage());
		}
		// send authors name:
		out.println("do server\nAuthors: André Röhrig, Samuel Gfrörer");
		
		
		String resultType = "RESULT";
		String result = "";
		// receive language + script to run:
		log.info("receiving command...");
		String exec = in.nextLine();
		String []temp = exec.split(" ");
		if( temp.length > 2 ) {
			resultType = "ERROR";
			result = "too many parameters";
			sendResult(resultType, result);
			return;
		}
		String application = temp[0];
		String filename = "";
		//String script = "";
		if(temp.length == 2 ) {
			filename = temp[1];
			
		}
		log.info("received: " + application + " " + filename);
		log.info("executing script...");
		// run script:
		Process p = null;
		try {
			if( filename != "" ) {
				createLocalFile(filename);
				p = runScript(application, filename);
			}
			else {
				p = runNoScript(application);
			}
		}
		catch( IOException | InterruptedException e ) {
			
			resultType = "ERROR";
			result = e.getMessage();
			log.log(Level.SEVERE, "exception while executing script");
			sendResult(resultType, result);
			return;
		}
		out.println("ok");
		log.info("done executing");
		
		// connect the io from the client to console:
		Scanner applicationOut = new Scanner(p.getInputStream());
		Scanner applicationErrOut = new Scanner(p.getErrorStream());
		InToOut appToOut = new InToOut(applicationOut, out);
		InToOut appErrToOut = new InToOut(applicationErrOut, out);
		new Thread(appToOut).start();
		new Thread(appErrToOut).start();
		
		PrintStream toApplication = new PrintStream( p.getOutputStream() );
		String current = null;
		while ( in.hasNext() && ! (current = in.nextLine()).equals(":q")) {
			toApplication.println(current);
			toApplication.flush();
		}
		log.info("exit session");
		appToOut.terminate();
		appErrToOut.terminate();
		
		/*applicationOut.close();
		applicationErrOut.close();*/
		
		// close connection:
		try {
			socket.close();
		}
		catch(IOException e) {
			log.log(Level.SEVERE, "exception while closing connection to client: " + e.getMessage());
		}
	}
	
	public void createLocalFile(String filename) throws IOException, InterruptedException {
		FileOutputStream file = new FileOutputStream(filename);
		PrintStream printFile = new PrintStream(file);
		//String EOF = in.nextLine();
		int countMax = Integer.parseInt(in.nextLine());
		
		String current = "";
		int countBytes = 0;
		while( countBytes < countMax ) {
			current = in.nextLine();
			printFile.println( current );
			countBytes += current.length() + 1;
		}
		printFile.close();
	}
	
	public Process runScript(String application, String filename) throws IOException {
		String command = application + " " + filename;
		log.info("executing: " + command);
		Process p = Runtime.getRuntime().exec(command);
		return p;
	}
	
	public Process runNoScript(String application) throws IOException {
		log.info("executing: " + application);
		return Runtime.getRuntime().exec(application);
	}
	public void sendResult(String resultType, String result) {
		out.println(resultType);
		out.println(result);
	}
	public Connection(Socket socket, Logger serverLog) {
		this.socket = socket;
		this.log = serverLog;
	}
	
	private Socket socket;
	private Logger log;
	
	private Scanner in;
	private PrintStream out;
}
