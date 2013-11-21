package udp;

public class UDPAddress {
	public UDPAddress(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	String getIP() {
		return ip;
	}
	int getPort() {
		return port;
	}
	private String ip;
	private int port;
}