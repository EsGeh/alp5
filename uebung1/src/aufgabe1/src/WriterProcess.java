
public class WriterProcess extends UsesBuffer {

	@Override
	void useBuffer() {
		System.out.println("trying to write");
		buffer.send(string);
		System.out.println("sent: \"" + string + "\"");
	}
	
	public WriterProcess(Buffer<String> buffer, String string) {
		super(buffer);
		this.string = string;
	}
	
	private String string;

}
