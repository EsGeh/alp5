package udp;

import java.net.InetAddress;

public class UDPAddress {
	public UDPAddress(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	InetAddress getIP() {
		return ip;
	}
	int getPort() {
		return port;
	}
	private InetAddress ip;
	private int port;
}