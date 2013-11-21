package udp;

import java.net.InetAddress;

public class RoutingEntry extends UDPAddress {

	public RoutingEntry(InetAddress ip, int port, int distance) {
		super(ip, port);
		this.distance = distance;
	}
	protected int distance;

}
