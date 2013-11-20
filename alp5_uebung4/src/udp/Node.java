package udp;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

import udp.UDPIn.InputInformation;

public class Node {

	public Node(
			String name,
			int inPort,
			String outIP
		) {
		//1. create a log:
		log = Logger.getLogger(this.getClass().getName() + "(" + name + ")" );
		try {
			FileHandler fh = new FileHandler(name + ".log",false);
			fh.setFormatter(new SimpleFormatter());
			fh.setLevel(Level.ALL);
			
			log.addHandler(fh);
			log.setLevel(Level.ALL);
		}
		catch(SecurityException | IOException e) {
			System.out.println("exception while creating log: " + e.getMessage());
		}
		
		//2. create the in/out Objects:
		in = new UDPInImpl<String>();
		out = new UDPOutImpl<String>();
		try {
			in.start(inPort);
			out.start(outIP);
		}
		catch(SocketException | UnknownHostException e) {
			log.log(Level.SEVERE, "exception caught: " + e.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// parse args:
		String name = args[0];
		int inPort = Integer.parseInt(args[1]);
		String outIP = args[2];
		int outPort = Integer.parseInt(args[3]);
		//create node:
		Node node = new Node(
				name,
				inPort,
				outIP
			);
	
		// run this node:
		node.log.info("Network node " + name + " started!");
		node.run(
				outPort
			);
	}
	
	private void run(int outPort) {
		boolean goOnRunning = true;
		try {
			do {
				log.info("waiting for messages...");
				InputInformation<String> inInfo = in.recv();
				log.info("received message!");
				String msg = inInfo.getMessage();
				switch(msg) {
					case "exit":
					{
						log.info("received: " + msg);
						goOnRunning = false;
						// redirect message to next node:
						out.send(
								outPort,
								msg
							);
					}
					break;
					default:
					{
						log.info("received: " + msg);
						// redirect message to next node:
						out.send(
								outPort,
								msg
							);
					}
					break;
				}
			} while (goOnRunning);
		}
		catch(ReceiveException e) {
			log.log(Level.SEVERE, "exception while receiving: " + e.getMessage());
		}
		catch(SendException e) {
			log.log(Level.SEVERE, "exception while sending: " + e.getMessage());
		}
		in.stop();
		out.stop();
	}
	
	private UDPIn<String> in;
	private UDPOut<String> out;
	
	private Logger log;
}
