package udp;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.activation.MailcapCommandMap;

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
	private BlockingQueue<InputInformation<String>> mailbox;
	
	
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
				if( isMetaMessage(inputInfo.message) ) {
					handleMetaMessage(inputInfo);
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
		
		private void handleMetaMessage(InputInformation<String> inputInfo) {
			
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
				outAddressList = new ArrayList<UDPAddress>(neighbours);
				outList = new ArrayList<UDPOut<String>>();
				for( UDPAddress addr : neighbours ) {
					UDPOut<String> out = new UDPOutImpl<String>();
					out.start(addr.getIP());
				}
			}
			catch(SocketException | UnknownHostException e) {
				System.out.println("exception while creating sockets: " + e.getMessage());
			}
		}
		
		boolean isMetaMessage(String msg) {
			return false;
		}
		public void exit() {
			in.stop();
			for( UDPOut<String> out : outList) {
				out.stop();
			}
		}
		
		private UDPIn<String> in;
		private List<UDPAddress> outAddressList;
		private List<UDPOut<String>> outList;
		
		private RoutingTable table;
		
		private BlockingQueue<InputInformation<String>> mailbox;
	}
}
