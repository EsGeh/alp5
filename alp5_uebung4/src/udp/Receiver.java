package udp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Receiver {

	/**
	 * @param args
	 * process name, inPort
	 */
	public static void main(String[] args) {
		if ( args.length < 2 || args.length % 2 != 0 ) {
			System.out.println("usage: Sender <name> <inPort> [ peerIP peerPort ...]");
			System.exit(1);
		}
		String name = args[0]; int inPort = Integer.parseInt(args[1]); 
		List<UDPAddress> peers= new ArrayList<UDPAddress>();
		try {
			for( int i=2; i<args.length; i+=2 ) {
				peers.add(
						new UDPAddress(InetAddress.getByName(args[i]), Integer.parseInt(args[i+1]))
					);
			}
		}
		catch( UnknownHostException e) {
			System.out.println("exception while looking up ip address of peer: " + e.getMessage());
			System.exit(1);
		}
		
		Comm.init(name, inPort, peers);
		
		// echo what is received:
		do {
			String message = Comm.RECV();
			System.out.println("received: " + message);
		}
		while(true);
	}
}
