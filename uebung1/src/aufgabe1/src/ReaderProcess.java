
public class ReaderProcess extends UsesBuffer {

	@Override
	void useBuffer() {
		System.out.println("trying to read");
		System.out.flush();
		String received = buffer.recv();
		System.out.println("received \"" + received + "\"");
	}
	
	public ReaderProcess(Buffer<String> buffer) {
		super(buffer);
	}

}
