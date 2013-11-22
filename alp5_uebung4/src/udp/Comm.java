package udp;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


import udp.Message.Type;



public class Comm {
	public static void init(
			String name,
			int inPort, 	// <- this is necessary! if there are more processes on the same machine, they have to listen on different ports!
			Collection<UDPAddress> peers
		) {
		create();
		pThis.helper = pThis.new Helper(
				name,
				inPort,
				peers
			);
		new Thread(pThis.helper).start();
	}
	
	public static void exit() {
		pThis.helper.exit();
	}
	
	public static void SEND(String message, String dest) {
		pThis.helper.send(dest, message);
	}
	
	public static String RECV() {
		//create();
		try {
			return pThis.helper.mailbox.take().getMessage();
		}
		catch(InterruptedException e) {
			System.out.println("exception while taking from mailbox: " + e.getMessage());
		}
		return "";
	}
	
	private static void create() {
		if( pThis == null) {
			pThis = new Comm();
		}
	}
	
	private static Comm pThis;
	
	// the helper process, receives any incoming messages, filters out and deals with meta messages, and therefore hides the protocol
	private Helper helper;
	
	
	/* receives all messages
	 * - deals with meta messages
	 * - normal messages are put into a BlockingQueue serving as a mailbox
	 */
	public class Helper implements Runnable {
		
		public void run() {
			sendHello();
			boolean goOnRunning = true;
			do {
				// receive any incoming messages:
				InputInformation<String> inputInfo = null;
				try {
					inputInfo = in.recv();
					//System.out.println("helper received something!");
				}
				catch(ReceiveException e) {
					System.out.println("exception while receiving: " + e.getMessage());
				}
				// handle it:
				Message msg = Message.fromString(
						inputInfo.getMessage(),
						inputInfo.getIP(),
						inputInfo.getSenderPort()
					);
				if( msg.type != Message.Type.NORMAL ) {
					handleMetaMessage( msg );
				} else {
					try {
						mailbox.put( inputInfo );
					}
					catch(InterruptedException e) {
						System.out.println("exception while putting message into Mailbox: " + e.getMessage());
					}
				}
			}
			while( goOnRunning );
			exit();
		}
		
		public void send(String dest, String message) {
			
			// lookup dest in the routing table:
			RoutingEntry entry = table.get(dest);
			if (entry == null) {
				System.out.println("node " + dest + " not found in routing table, aborting send... ");
				System.out.println(table.toString());
				return;
			}
			// check if we have already a socket to the dest:
			UDPOut<String> out = pThis.helper.outFromAddress.get(entry);
			if( out == null ) {
				// if not, create one, run it, and add it to the list of output sockets:
				out = new UDPOutImpl<String>();
				try {
					out.start(entry.getIP().getHostName());
				}
				catch(SocketException | UnknownHostException e) {
					System.out.println("exception while adding new output: " + e.getMessage());
				}
				outFromAddress.put(entry,out);
			}
			// send the message as a non-meta message:
			try {
				out.send(
						entry.getPort(),
						"NORMAL\n" + message
					);
			} catch (SendException e) {
				System.out.println("exception while sending: " + e.getMessage());
			}
		}
		
		public void sendHello() {
			System.out.println("sending hello...");
			String ipThis = "";
			try {
				ipThis = InetAddress.getLocalHost().getHostAddress();
			}
			catch( UnknownHostException e) {
				System.out.println("exception while creating HELLO: " + e.getMessage());
			}
			Message msg = new Message(
					Type.HELLO,
					name + "\n" + ipThis + "\n" + Integer.toString(in.getPort()),
					in.getIP(),
					in.getPort()
				);
			try {
				for( Map.Entry<UDPAddress, UDPOut<String>> out : outFromAddress.entrySet() ) {
					out.getValue().send(
							out.getKey().getPort(),
							msg.toString()
						);
				}
			}
			catch(SendException e) {
				System.out.println("exception while spreading hello: " + e.getMessage());
			}
		}
		
		private void handleMetaMessage( Message msg ) {
			if( msg.type == Message.Type.HELLO  ) {
				Message.Hello hello = null;
				try {
					hello = msg.asHello();
					table.put(
							hello.getName(),
							new RoutingEntry(
								hello.getSource().getIP(),
								hello.getSource().getPort(),
								1
							)
						);
					
				}
				catch( SyntaxException | UnknownHostException e) {
					System.out.println("exception while parsing HELLO: " + e.getMessage());
				}
				try {
					UDPOut<String> out = new UDPOutImpl<String>();
					out.start(hello.getSource().getIP().getHostName());
					outFromAddress.put(
							hello.getSource(),
							out
						);
				}
				catch(SocketException | UnknownHostException e) {
					System.out.println("exception while adding UDPOut: " + e.getMessage());
				}
				spreadUpdate();
			} else if ( msg.type == Message.Type.UPDATE ) {
				try {
					if( table.update(
							RoutingTable.fromString(msg.content)
						) ) {
						spreadUpdate();
					}
				}
				catch( SyntaxException e ) {
					System.out.println("syntax error in update message: " + e.getMessage());
				}
			}
		}
		
		void spreadUpdate() {
			//System.out.println("spread update...");
			String content = table.toString();
			Message msg = new Message(
					Type.UPDATE,
					content,
					in.getIP(),
					in.getPort()
				);
			try {
				for( Map.Entry<UDPAddress, UDPOut<String>> out : outFromAddress.entrySet() ) {
					out.getValue().send(
							out.getKey().getPort(),
							msg.toString()
						);
				}
			}
			catch(SendException e) {
				System.out.println("exception while spreading Update: " + e.getMessage());
			}
		}
		
		public Helper(
				String name,
				int inPort,
				Collection<UDPAddress> neighbours
			) {
			
			this.name = name;
			
			table = new RoutingTable();
			mailbox = new ArrayBlockingQueue<InputInformation<String>>(100);
			
			// enter myself into the routing table:
			InetAddress me = null;
			try {
				me = InetAddress.getLocalHost();
			}
			catch(UnknownHostException e) {
				System.out.println("exception while finding out my own ip address: " + e.getMessage());
			}
			table.put(name,
					new RoutingEntry(
						me,
						inPort,
						0
					)
				);
			// create sockets to all peers:
			try {
				in = new UDPInImpl<String>();
				in.start(inPort);
				outFromAddress = new ConcurrentHashMap<UDPAddress,UDPOut<String>>();
				for( UDPAddress addr : neighbours ) {
					
					//OutputInfo outInfo = new OutputInfo(addr.getPort());
					UDPOut<String> out = new UDPOutImpl<String>();
					out.start(addr.getIP().getHostName());
					outFromAddress.put(addr,out);
				}
			}
			catch(SocketException | UnknownHostException e) {
				System.out.println("exception while creating sockets: " + e.getMessage());
			}
		}
		
		public void exit() {
			in.stop();
			for( UDPOut<String> out : outFromAddress.values()) {
				out.stop();
			}
		}
		
		// the mnemonic name for this node:
		private String name;
		
		// socket to receive incoming messages
		private UDPIn<String> in;
		
		// name -> ( udpAddr , distance )
		private RoutingTable table;
		
		// sockets to all peers, indexed by their address ( udpAddr -> UDPOut )
		private Map<UDPAddress,UDPOut<String>> outFromAddress;
		
		// mailbox for all (non-meta) messsages received:
		private BlockingQueue<InputInformation<String>> mailbox;
		
	}
}
