package udp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sender {

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
		
		// ask user to enter a string:
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.print("dest (or exit): ");
			String dest = in.next();
			if( dest.equals("exit") )
				break;
			System.out.print("message (or exit): ");
			String message = in.next();
			if( message.equals("exit") )
				break;
			Comm.SEND(message, dest);
		}
		in.close();
		Comm.exit();
		System.out.println("done");
	}
}