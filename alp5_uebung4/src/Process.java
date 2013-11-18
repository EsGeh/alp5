
public interface Process<M> {
	// starts the thread, passing some peers:
	void start(Process<M>[] peers);
	// sends a message, possibly blocking:
	void send(M message);
}
