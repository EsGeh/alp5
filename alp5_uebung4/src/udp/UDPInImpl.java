package udp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPInImpl<M> implements UDPIn<M> {

	@Override
	public void start(int portToListenOn) throws SocketException {
		socket = new DatagramSocket(portToListenOn);
	}

	@Override
	public void stop() {
		socket.close();
	}

	@Override
	public InputInformation<M> recv() throws ReceiveException {
		try {
			byte[] inData = new byte[1024];
			DatagramPacket packet = new DatagramPacket(inData, 1024);
			socket.receive(packet);
			return new InputInformation<M>(
					socket.getInetAddress(),
					packet.getPort(),
					fromByteRepr(packet.getData())
				);
		}
		catch(IOException | SerializingException e) {
			throw new ReceiveException(e);
		}
	}
	
	@Override
	public InetAddress getIP() {
		return socket.getLocalAddress();
	}

	@Override
	public int getPort() {
		return socket.getLocalPort();
	}
	
	private M fromByteRepr(byte[] data) throws SerializingException{
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(in);
			M ret = (M )ois.readObject();
			ois.close();
			return ret;
		}
		catch(IOException | ClassNotFoundException e) {
			throw new SerializingException(e.getMessage());
		}
	}
	
	private DatagramSocket socket;
}
