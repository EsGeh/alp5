package process;


public class KnowsPeers<M>
	extends Thread
{
	
	public void start(Process<M>[] peers) {
		this.peers = peers;
	}
	
	protected Process<M>[] getPeers() {
		return peers;
	}
	
	private Process<M> peers[];
}
