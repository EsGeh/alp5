import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
//import java.util.Set;
import java.util.Scanner;


public class DictionaryImpl
	extends HashMap<String, LinkedList<String>>
	implements Dictionary { // (just to mention)

	/*public DictionaryImpl() throws RemoteException {
		super();
	}*/
	
	public void init(String filename) throws FileNotFoundException {
		Scanner fileIn = new Scanner(new FileInputStream(filename));
		while( fileIn.hasNext() ) {
			String line = fileIn.nextLine();
			String[] wordsInLine = line.split(" ");
			if( wordsInLine.length == 0) {
				System.out.println("skipping line: \"" + line + "\"");
				break;
			}
				
			
			String index = wordsInLine[0];
			LinkedList<String> result = new LinkedList<String>();
			for( int i=0; i<wordsInLine.length-1; i++) {
				result.add(wordsInLine[1+i]);
			}
			this.put(index, result);
		}
	}
	
	//public 

	@Override
	public LinkedList<String> lookup(String word) throws RemoteException {
		return this.get(word);
	}

}
