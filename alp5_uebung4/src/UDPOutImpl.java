import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDPOutImpl<M extends Serializable> implements UDPOut<M>
{
	@Override
	public void start(String ip) throws UnknownHostException, SocketException{
		destAddress = InetAddress.getByName(ip);
		socket = new DatagramSocket();
	}
	public void stop() {
		socket.close();
	}

	@Override
	public void send(int port, M message) throws SendException {
		try {
			byte[] message_ = getByteRepr(message);
			DatagramPacket packet = new DatagramPacket(
					message_,
					message_.length,
					destAddress,
					port
				);
			socket.send(packet);
		}
		catch(IOException | SerializingException e) {
			throw new SendException(e.getMessage());
		}
	}
	
	private byte[] getByteRepr(Object o) throws SerializingException {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(o);
			return out.toByteArray();
		}
		catch(IOException e) {
			throw new SerializingException(e.getMessage());
		}
	}
	private InetAddress destAddress;
	private DatagramSocket socket;
}