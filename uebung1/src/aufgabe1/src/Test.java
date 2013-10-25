import java.io.IOException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hallo Welt!");
		Buffer<String> buffer = null;
		try {
			buffer= new StringBufferImpl(2);
		} catch( StringBufferImpl.InvalidSizeException e) {
			System.out.println("invalid buffer size!");
		}
		/*buffer.send("Hallo Puffer");
		buffer.send("hallo Puffer 2");
		buffer.send("hallo Puffer 3");
		System.out.println(buffer.recv());
		System.out.println(buffer.recv());*/
		
		/*ReaderProcess reader = new ReaderProcess(buffer);
		new Thread(reader).start();*/
		
		// create reader and writer processes:
		ReaderProcess[] readers = new ReaderProcess[readerCount];
		WriterProcess[] writers = new WriterProcess[writerCount];
		for( int iReader = 0 ; iReader < readerCount; iReader++ ) {
			readers[iReader] = new ReaderProcess(buffer);
			new Thread(readers[iReader]).start();
		}
		for( int i = 0 ; i < writerCount; i ++ ) {
			writers[i] = new WriterProcess(buffer, "nachricht von " + i);
			new Thread(writers[i]).start();
		}
		try {
			System.in.read();
		} catch( IOException e) {
			e.printStackTrace();
		}
		//reader.stop();
		for( ReaderProcess r : readers) {
			r.stop();
		}
		for( WriterProcess w : writers) {
			w.stop();
		}
	}
	private static final int readerCount = 2;
	private static final int writerCount = 2;

}
