package udp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestUDP {

	public TestUDP() {
		List<Process> procs = new ArrayList<Process>();
		try {
			for( int i=0; i<countProcess; i++) {
				int inPort = basePort + i;
				String outIP = "localhost"; // "127.0.0.1";
				int outPort = basePort + ((i+1) % countProcess);
				String command = 
						"java udp.Node " +
						Integer.toString(i) + " " +
						Integer.toString(inPort) + " " +
						outIP + " " +
						Integer.toString(outPort);
				System.out.println("execute: " + command);
				Process proc = Fork.fork(
						command
					);
				procs.add(proc);
			}
		}
		catch(IOException e) {
			System.out.println("exception while creating the processes: " + e.getMessage());
		}
		// flood the ring with the "exit" message:
		UDPOut<String> out = new UDPOutImpl<String>();
		try {
			out.start("localhost");
		} catch (UnknownHostException | SocketException e) {
			System.out.println("exception while connecting to the ring: " + e.getMessage());
		}
		try {
			Thread.sleep(5000);
		}
		catch(InterruptedException e) {
			System.out.println("exception while trying to wait: " + e.getMessage());
		}
		System.out.println("flooding the ring... ");
		try {
			out.send(
					basePort,
					//"hallo"
					"exit"
				);
		} catch (SendException e) {
			System.out.println("exception while flooding the ring: " + e.getMessage());
		}
		// wait for the remote processes to finish:
		System.out.println("waiting for all processes to finish...");
		try {
			for( int i=0; i<countProcess; i++) {
				Process proc = procs.get(i);
				proc.waitFor();
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		out.stop();
		System.out.println("done");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestUDP();
	}

	public static final int countProcess = 5 ;
	public static final int basePort = 8888;
}
