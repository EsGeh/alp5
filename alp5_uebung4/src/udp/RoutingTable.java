package udp;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class RoutingTable extends HashMap<String,UDPAddress>{
	
	public RoutingTable() {
		super();
	}
	
	public void setFromString(String string) throws SyntaxException {
		this.clear();
		this.putAll(fromString(string));
	}

	public static RoutingTable fromString(String string) throws SyntaxException {
		RoutingTable ret = new RoutingTable();
		for( String line : string.split("\n")) {
			String name, ip, port;
			String words[] = line.split(" ");
			if( words.length != 3 ) {
				throw new SyntaxException("wrong line format!");
			}
			name = words[0]; ip = words[1]; port = words[2];
			try {
				ret.put(
						name, 
						new UDPAddress(InetAddress.getByName(ip), Integer.parseInt(port))
					);
			}
			catch(Exception e) {
				throw new SyntaxException(e);
			}
		}
		return ret;
	}
	public String toString() {
		String ret = "\n";
		for( Map.Entry<String, UDPAddress> entry : this.entrySet()) {
			ret = ret + entry.getKey() + " " + entry.getValue().getIP() + " " + entry.getValue().getPort() + "\n" ;
		}
		return ret.substring(0, ret.length() - 2);
	}
	
	public boolean update( RoutingTable other ) {
		return false;
	}
	
	/*public int getFreePort() {
		int ret = 8000;
		for( UDPAddress addr : this.values()) {
			ret = Math.max(ret, addr.getPort());
			ret += 1;
		}
		return ret;
	}*/
	
	private static final long serialVersionUID = 1L;
}
