package udp;
import java.net.InetAddress;
import java.net.SocketException;


public interface UDPIn<M> {
	void start(int portToListenOn) throws SocketException;
	void stop();
	
	InputInformation<M> recv() throws ReceiveException;
	
	int getPort();
	InetAddress getIP();
}
