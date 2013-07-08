package estabrook.chessclub.chesspairs;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class ChessPairs {

	public static final String CHESS_PAIRS = "Chess Pairs";
	public static final FileNameExtensionFilter FILE_NAME_EXTENSION_FILTER = new FileNameExtensionFilter("Chess Tournament file", "cht");
	private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ChessPairs.class);
	private static final String CURRENT_DIRECTORY = "currentDirectory";
	private static final String USER_HOME = System.getProperty("user.home");
	private static File currentDirectory;
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {;
				
		currentDirectory = new File(PREFERENCES.get(CURRENT_DIRECTORY, USER_HOME));
		
		if (args.length > 0) {
			Tournament t = Tournament.readFromFile(new File(args[0]));
			t.findBestPairing();
			t.printBestPairing();
			t.printPlayerInfo();
		} else {
			new StartupOptions();
		}
		
		PREFERENCES.put(CURRENT_DIRECTORY, currentDirectory.getAbsolutePath());
	}
	
	public static File getCurrentDirectory() {
		return currentDirectory;
	}
	
	public static void setCurrentDirectory(File f) {
		currentDirectory = f;
	}
	
}
