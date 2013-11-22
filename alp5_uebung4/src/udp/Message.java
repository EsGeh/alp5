package udp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Message {
	public enum Type { HELLO, UPDATE, NORMAL };
	
	private Message() {}
	
	public Message(Message msg) {
		this.type = msg.type;
		this.content = msg.content;
		
		this.sourceIP = msg.sourceIP;
		this.sourcePort = msg.sourcePort;
	}
	
	public Message(Type type, String content, InetAddress sourceIP, int sourcePort) {
		this.type = type;
		this.content = content;
		
		this.sourceIP = sourceIP;
		this.sourcePort = sourcePort;
	};
	
	public Hello asHello() throws SyntaxException, NumberFormatException, UnknownHostException {
		String []lines = content.split("\n");
		if( lines.length != 3) {
			throw new SyntaxException("format must be three lines!");
		}
		return new Hello(
				lines[0],
				new UDPAddress(
					InetAddress.getByName(lines[1]),
					Integer.parseInt(lines[2])
				)
			);
	}
	
	public static Message fromString(
			String string,
			InetAddress sourceIP,
			int sourcePort
		) {
		Message ret = new Message();
		ret.sourceIP = sourceIP;
		ret.sourcePort = sourcePort;
		
		String []lines = string.split("\n",2);
		if( lines.length == 0 || lines[0].equals("NORMAL") ) {
			ret.type = Type.NORMAL;
		} else if ( lines[0].equals( "HELLO" ) ) {
			ret.type = Type.HELLO;
		} else if ( lines[0].equals("UPDATE") ){
			ret.type = Type.UPDATE;
		}
		ret.content = lines[1];
		return ret;
	}
	public String toString() {
		return type + "\n" + content;
	}
	
	public class Hello {
		public Hello(String name, UDPAddress src) {
			this.name = name;
			this.src = src;
		}
		public String getName(){ return name;}
		public UDPAddress getSource() { return src; }
		private String name;
		private UDPAddress src;
	}
	
	protected InetAddress sourceIP;
	protected int sourcePort;
	protected Type type;
	protected String content;
}
