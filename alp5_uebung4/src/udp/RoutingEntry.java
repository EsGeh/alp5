package udp;

import java.net.InetAddress;

public class RoutingEntry extends UDPAddress {

	public RoutingEntry(InetAddress ip, int port, int distance) {
		super(ip, port);
		this.distance = distance;
	}
	public int getDistance() {
		return distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	private int distance;

}
