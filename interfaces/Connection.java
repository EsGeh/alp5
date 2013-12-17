import java.io.PrintStream;
import java.util.Scanner;


public interface Connection {
	
	public Scanner getIn();
	public PrintStream getOut();
	
	/*public String recv();
	public void send(String message);*/
	
}
