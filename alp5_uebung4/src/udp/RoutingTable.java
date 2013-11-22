package udp;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable extends ConcurrentHashMap<String,RoutingEntry>{
	
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
			String name, ip, port, dist;
			String words[] = line.split(" ");
			if( words.length != 4 ) {
				throw new SyntaxException("wrong line format!");
			}
			name = words[0]; ip = words[1]; port = words[2]; dist = words[3];
			try {
				ret.put(
						name, 
						new RoutingEntry(InetAddress.getByName(ip), Integer.parseInt(port), Integer.parseInt(dist))
					);
			}
			catch(Exception e) {
				throw new SyntaxException(e);
			}
		}
		return ret;
	}
	public String toString() {
		String ret = "";
		for( Map.Entry<String, RoutingEntry> entry : this.entrySet()) {
			ret = ret + entry.getKey() + " " + entry.getValue().getIP().getHostAddress() + " " + entry.getValue().getPort() + " " + entry.getValue().getDistance() + "\n";
		}
		return ret; //ret.substring(0, ret.length() - 3);
	}
	
	public boolean update( RoutingTable other ) {
		//System.out.println("other routing table:");
		//System.out.println( other.toString() );
		boolean hasChanged = false;
		for( Map.Entry<String, RoutingEntry> otherEntry : other.entrySet()) {
			String name = otherEntry.getKey();
			RoutingEntry otherValue = otherEntry.getValue();
			RoutingEntry entry =  this.get(name);
			if( entry != null ) {
				if( otherValue.getDistance() + 1 < entry.getDistance() ) {
					//update value:
					this.put(
							name,
							new RoutingEntry(
									otherValue.getIP(),
									otherValue.getPort(),
									otherValue.getDistance() + 1
								)
						);
					hasChanged = true;
				}
			}
			else {
				this.put(
						name,
						new RoutingEntry(
								otherValue.getIP(),
								otherValue.getPort(),
								otherValue.getDistance() + 1
							)
					);
				hasChanged = true;
			}
		}
		return hasChanged;
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
