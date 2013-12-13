import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class LanguageChecker {

	public LanguageChecker() {
	}

	/**
	 * @param args
	 * LanguageChecker <yourIP> <file> <host1> <host2>
	 */
	public static void main(String[] args) {
		if( args.length != 4) {
			System.out.println("syntax: LanguageChecker localIP filename host1 host2");
			return;
		}
		String localIP = args[0];
		String germanDict = "german"; String englishDict = "english";
		String filename = args[1];
		String host1 = args[2]; String host2 = args[3];
		
		LanguageChecker pThis = new LanguageChecker();
		try {
			pThis.connect(filename);
		}
		catch(IOException e) {
			System.out.println("exception while connecting: " + e.getMessage());
			return;
		}
		try {
			pThis.exec(localIP, germanDict,englishDict, host1, host2);
		}
		catch(Exception e) {
			System.out.println("exception while executing: " + e.getMessage());
			return;
		}
		
		//String host2 = args[3]; int port2 = Integer.parseInt(args[4]);
	}
	
	public void connect(String filename) throws IOException {
		inFromFile = new Scanner(new FileInputStream(filename));
	}
	
	public void exec(String localIP, String germanDict, String englishDict, String host1, String host2) throws Exception {
		// -s : input from tcp
		// -c host port : output to tcp
		
		
		
		Process pEnglish1 = null;
		try {
			String commandEnglish1 = "java -classpath bin Filter + " + englishDict + " -s";
			if( ! host1.equals("localhost"))
				commandEnglish1 = host1 + ":" + commandEnglish1 + " -c " + localIP + " 8000";
			System.out.println("command: " + commandEnglish1);
			System.out.println("fork...");
			pEnglish1 = Fork.fork(  commandEnglish1);
			System.out.println("fork done");
		}
		catch(IOException e) {
			throw new Exception("exception while forking remote process: " + e.getMessage());
		}
		
		int portEnglish1 = -1;
		if( !host1.equals("localhost")) {
			
			Scanner errFromEnglish1 = new Scanner(pEnglish1.getErrorStream());
			System.out.println("scanner created");
			try {
				String errFromEngl1 = errFromEnglish1.nextLine();
				//System.out.println("from stderr: " + errFromEngl1);
				portEnglish1 = Integer.parseInt(errFromEngl1); //errFromEnglish1.nextInt();
			}
			catch(Exception e) {
				errFromEnglish1.close();
				throw new Exception(" exception while receiving from remote err: " + e.getMessage());
			}
			System.out.println("port: " + portEnglish1);
		}
		
		Socket socket = null;
		Scanner fromEngl1 = null;
		try {
			if( !host1.equals("localhost")) {
				socket = new Socket(host1, portEnglish1);
				fromEngl1 = new Scanner(socket.getInputStream());
			}
			else {
				fromEngl1 = new Scanner(pEnglish1.getInputStream());
			}
		}
		catch(IOException e) {
			throw new Exception("exception while trying to connect to remote process: " + e.getMessage());
		}
		
		InToOut engl1ToOut  = new InToOut(fromEngl1, System.out);
		Thread engl1ToOutThread = new Thread(engl1ToOut);
		engl1ToOutThread.start();
		
		pEnglish1.waitFor();
		
		engl1ToOutThread.join();
		System.out.println("all threads finished!");
		
		socket.close();
		//System.out.println("port of engl1: " + portEnglish1);
	}
	
	/*private InetAddress getOwnIP() throws UnknownHostException {
		return InetAddress.getByName("localhost");
		//return InetAddress.getLocalHost();
	}*/
	
	private Scanner inFromFile;
}
