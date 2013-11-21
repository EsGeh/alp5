package udp;

import java.util.HashMap;
import java.util.Map;

public class RoutingTable extends HashMap<String,UDPAddress>{
	
	public RoutingTable() {
		super();
	}

	public void fromString(String string) throws SyntaxException {
		for( String line : string.split("\n")) {
			String name, ip, port;
			String words[] = line.split(" ");
			if( words.length != 3 ) {
				throw new SyntaxException("wrong line format!");
			}
			name = words[0]; ip = words[1]; port = words[2];
			this.put(
					name, 
					new UDPAddress(ip, Integer.parseInt(port))
				);
		}
	}
	public String toString() {
		String ret = "\n";
		for( Map.Entry<String, UDPAddress> entry : this.entrySet()) {
			ret = ret + entry.getKey() + " " + entry.getValue().getIP() + " " + entry.getValue().getPort() + "\n" ;
		}
		return ret.substring(0, ret.length() - 2);
	}
	
	public int getFreePort() {
		int ret = 8000;
		for( UDPAddress addr : this.values()) {
			ret = Math.max(ret, addr.getPort());
			ret += 1;
		}
		return ret;
	}
	
	private static final long serialVersionUID = 1L;
}
