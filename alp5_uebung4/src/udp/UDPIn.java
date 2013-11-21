package udp;
import java.net.InetAddress;
import java.net.SocketException;


public interface UDPIn<M> {
	void start(int portToListenOn) throws SocketException;
	void stop();
	
	InputInformation<M> recv() throws ReceiveException;
	
	int getPort();
	InetAddress getIP();
	
	public class InputInformation<M> {
		InetAddress getIP() { return ip; };
		int getSenderPort() { return port; };
		M getMessage() { return message; };
		protected InetAddress ip;
		protected int port;
		protected M message;
	}
}
