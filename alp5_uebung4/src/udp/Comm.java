package udp;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


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
		
		RoutingEntry entry = pThis.helper.table.get(dest);
		if (entry == null) {
			System.out.println("node " + dest + " not found in routing table, aborting send... ");
			return;
		} 
		UDPOut<String> out = pThis.helper.outFromAddress.get(entry);
		if( out == null ) {
			System.out.println("no output for node " + dest + ", aborting send... ");
			return;
		}
		try {
			out.send(
					entry.getPort(),
					message
				);
		} catch (SendException e) {
			System.out.println("exception while sending: " + e.getMessage());
		}
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
	
	private Helper helper;
	//private BlockingQueue<InputInformation<String>> mailbox;
	
	
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
					System.out.println("helper received something!");
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
		}
		
		public void sendHello() {
			System.out.println("sending hello...");
			Message msg = new Message(
					Type.HELLO,
					name + "\n" + in.getIP().getHostAddress() + "\n" + Integer.toString(in.getPort()),
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
			System.out.println("spread update...");
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
			try {
				in = new UDPInImpl<String>();
				in.start(inPort);
				outFromAddress = new HashMap<UDPAddress,UDPOut<String>>();
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
		
		boolean isHello(String msg) {
			String []lines = msg.split("\n");
			return (
					lines.length > 0 && lines[0] == "hello"
				);
		}
		boolean isUpdate(String msg) {
			String []lines = msg.split("\n");
			return (
					lines.length > 0 && lines[0] == "hello"
				);
		}
		public void exit() {
			in.stop();
			for( UDPOut<String> out : outFromAddress.values()) {
				out.stop();
			}
		}
		
		private String name;
		
		private UDPIn<String> in;
		//private List<UDPAddress> outAddressList;
		//private List<OutputInfo> outList;
		
		private RoutingTable table;
		private Map<UDPAddress,UDPOut<String>> outFromAddress;
		
		private BlockingQueue<InputInformation<String>> mailbox;
		
		/*public class OutputInfo {
			public OutputInfo(int port) {
				this.port = port;
			}
			protected int port;
			protected UDPOut<String> out;
		}*/
	}
}
