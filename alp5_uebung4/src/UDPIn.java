import java.net.SocketException;


public interface UDPIn<M> {
	void start(int portToListenOn) throws SocketException;
	void stop();
	
	InputInformation<M> recv() throws ReceiveException;
	
	public class InputInformation<M> {
		int getSenderPort() { return port; };
		M getMessage() { return message; };
		protected int port;
		protected M message;
	}
}
