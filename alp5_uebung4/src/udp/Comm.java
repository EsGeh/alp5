package udp;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


import udp.Message.Type;
import udp.UDPIn.InputInformation;



public class Comm {
	public static void init(
			String name,
			int inPort, 	// <- this is necessary! if there are more processes on the same machine, they have to listen on different ports!
			UDPAddress[] peers
		) {
		create();
		pThis.helper = pThis.new Helper(
				inPort,
				Arrays.asList(peers)
			);
		new Thread(pThis.helper).start();
	}
	
	public static void SEND(String message, String dest) {
		//create();
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
			boolean goOnRunning = true;
			do {
				// receive any incoming messages:
				InputInformation<String> inputInfo = null;
				try {
					inputInfo = in.recv();
				}
				catch(ReceiveException e) {
					System.out.println("exception while receiving: " + e.getMessage());
				}
				// handle it:
				Message msg = Message.fromString(
						inputInfo.message,
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
		
		private void handleMetaMessage( Message msg ) {
			if( msg.type == Message.Type.HELLO  ) {
				table.put(
						msg.content,
						new RoutingEntry(
							msg.sourceIP,
							msg.sourcePort,
							1
						)
					);
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
			Message msg = new Message(
					Type.UPDATE,
					table.toString(),
					in.getIP(),
					in.getPort()
				);
			try {
				for( OutputInfo out : outList ) {
					out.out.send(
							out.port,
							msg.toString()
						);
				}
			}
			catch(SendException e) {
				System.out.println("exception while spreading Update: " + e.getMessage());
			}
		}
		
		public Helper(
				int inPort,
				Collection<UDPAddress> neighbours
			) {
			
			table = new RoutingTable();
			mailbox = new ArrayBlockingQueue<InputInformation<String>>(100);
			try {
				in = new UDPInImpl<String>();
				in.start(inPort);
				//outAddressList = new ArrayList<UDPAddress>(neighbours);
				outList = new ArrayList<OutputInfo>();
				for( UDPAddress addr : neighbours ) {
					OutputInfo outInfo = new OutputInfo(addr.getPort());
					//UDPOut<String> out = new UDPOutImpl<String>();
					outInfo.out.start(addr.getIP().getHostName());
					outList.add(outInfo);
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
			for( OutputInfo out : outList) {
				out.out.stop();
			}
		}
		
		private UDPIn<String> in;
		//private List<UDPAddress> outAddressList;
		private List<OutputInfo> outList;
		
		private RoutingTable table;
		
		private BlockingQueue<InputInformation<String>> mailbox;
		
		public class OutputInfo {
			public OutputInfo(int port) {
				this.port = port;
			}
			protected int port;
			protected UDPOut<String> out;
		}
	}
}
