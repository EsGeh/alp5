package udp;

import java.net.InetAddress;

public class Message {
	public enum Type { HELLO, UPDATE, NORMAL };
	
	private Message() {}
	
	public Message(Type type, String content, InetAddress sourceIP, int sourcePort) {
		this.type = type;
		this.content = content;
		
		this.sourceIP = sourceIP;
		this.sourcePort = sourcePort;
	};
	
	public static Message fromString(
			String string,
			InetAddress sourceIP,
			int sourcePort
		) {
		Message ret = new Message();
		ret.sourceIP = sourceIP;
		ret.sourcePort = sourcePort;
		
		String []lines = string.split("\n",1);
		if( lines.length == 0 || lines[0] == "normal" ) {
			ret.type = Type.NORMAL;
		} else if ( lines[0] == "hello" ) {
			ret.type = Type.HELLO;
		} else if ( lines[0] == "update") {
			ret.type = Type.UPDATE;
		}
		return ret;
	}
	public String toString() {
		return type + "\n" + content;
	}
	
	protected InetAddress sourceIP;
	protected int sourcePort;
	protected Type type;
	protected String content;
}
