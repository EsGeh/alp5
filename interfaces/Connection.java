import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;


public interface Connection {
	
	public boolean isOpen();
	
	public void close() throws IOException;
	
	public Scanner getIn();
	public PrintStream getOut();
	
	/*public String recv();
	public void send(String message);*/
	
}
