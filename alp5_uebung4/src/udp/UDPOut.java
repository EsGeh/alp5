package udp;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public interface UDPOut<M> {
	void start(String ip) throws UnknownHostException, SocketException;
	void stop();
	void send(
		int port,
		M message
	) throws SendException;
	
	InetAddress getIP();
	//void send(String ip, String port);
}