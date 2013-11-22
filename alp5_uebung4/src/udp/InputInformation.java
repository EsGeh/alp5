package udp;

import java.net.InetAddress;

public class InputInformation<M> {
	public InputInformation(InetAddress ip, int port, M message) {
		this.ip = ip;
		this.port = port;
		this.message = message;
	}
	public InputInformation(InputInformation<M> other) {
		this.ip = other.ip;
		this.port = other.port;
		this.message = other.message;
	}
	InetAddress getIP() { return ip; };
	int getSenderPort() { return port; };
	M getMessage() { return message; };
	private InetAddress ip;
	private int port;
	private M message;
}