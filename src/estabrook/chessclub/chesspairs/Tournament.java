package estabrook.chessclub.chesspairs;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Tournament {

	private static final String TOURNAMENT = "tournament";
	private static final String PLAYER = "player";
		
	private static final Random random = new Random();
	
	private final Player[] players;
	private final boolean[][] flipSequences;
	private Pair[] bestPairing;
	
	float bestStaticScore = Float.POSITIVE_INFINITY;
	float bestDynamicScore = Float.POSITIVE_INFINITY;
	List<Pair[]> bestPairings = null;
	List<Pair[]> bestColoredPairings = null;
	
	public Tournament(Element tournamentXML) {
		
		NodeList playersXML = tournamentXML.getElementsByTagName(PLAYER);
		players = new Player[playersXML.getLength()];
		Player p;
		for (int i = 0; i < players.length; i++) {
			p = new Player((Element) playersXML.item(i));
			players[i] = p;
		}
		int n = players.length / 2;
		flipSequences = (boolean[][]) permuteFlipSequences(new boolean[n], n).toArray(new boolean[0][]);
		bestPairing = new Pair[n];
	}
	
	public Tournament(List<Player> players) {
		if (players.size() % 2 == 1) players.add(Player.UNPAIRED);
		this.players = (Player[]) players.toArray(new Player[0]);
		int n = this.players.length / 2;
		flipSequences = (boolean[][]) permuteFlipSequences(new boolean[n], n).toArray(new boolean[0][]);
		bestPairing = new Pair[n];
	}
	
	public static Tournament readFromFile(File file) throws SAXException, IOException, ParserConfigurationException {
		
		Document doc = null;
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		doc.normalize();
		return new Tournament((Element) doc.getElementsByTagName(TOURNAMENT).item(0));
	}
	
	public void save(File file) throws ParserConfigurationException, TransformerException, TransformerFactoryConfigurationError {
				
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	
		doc.appendChild(toXML(doc));
		Transformer transformer = null;
		transformer = TransformerFactory.newInstance().newTransformer();
				
		transformer.transform(new DOMSource(doc), new StreamResult(file));
	}
	
	public void saveAsPDF(File file) throws FileNotFoundException, DocumentException {
				
		com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(PageSize.LETTER);
		PdfWriter.getInstance(pdf, new FileOutputStream(file + ".pdf"));
		pdf.open();
		pdf.add(generateBestPairingTable());
		pdf.add(new Paragraph("\n"));
		pdf.add(generatePlayerInfoTable());
		pdf.close();
	}
	
	public Player[] getPlayers() {
		return players;
	}
		
	public void findBestPairing() {
		
		boolean haveGames = false;
		for (Player p : players) {
			if (!p.getGames().isEmpty()) {
				haveGames = true;
				break;
			}
		}
		
		if (haveGames) {
			findBestPairings(players, null);
			findBestDynamicPairings();
			chooseBestPairing();
		} else {
			
			Player[] players = Arrays.copyOf(this.players, this.players.length);
			shuffle(players);
			for (int i = 0; i < players.length; i += 2) {
				bestPairing[i / 2] = new Pair(players[i], players[i + 1]);
			}
		}
	}
	
	public void chooseBestPairing() {
		bestPairing = bestColoredPairings.get(random.nextInt(bestColoredPairings.size()));
	}
	
	public Pair[] getBestPairing() {
		return bestPairing;
	}
	
	@SuppressWarnings("unchecked")
	private void findBestPairings(Player[] remainder, ArrayList<Pair> partial) {
		
		ArrayList<Pair> newPartial;
		
		if (partial == null) {
			partial = new ArrayList<Pair>();
		}
		
		if (remainder.length == 0) {
			
			Pair[] pairing = partial.toArray(new Pair[0]);
			float staticScore = staticallyScorePairing(pairing);
			
//			System.out.println(staticScore);
//			printPairing(pairing);
			
			if (staticScore < bestStaticScore) {
				
				bestStaticScore = staticScore;
				bestPairings = new ArrayList<Pair[]>();
				bestPairings.add(pairing);
				
			} else if (staticScore == bestStaticScore) {
				
				bestPairings.add(pairing);
				
//				if (bestPairings.size() > 16) {
//					Pair[] temp = bestPairings.get(random.nextInt(17));
//					bestPairings = new ArrayList<Pair[]>();
//					bestPairings.add(temp);
//				}

			}
			
		} else {
			
			Player[] r1, r2;
			
			for (int i = 1; i < remainder.length; i++) {
			
				newPartial = (ArrayList<Pair>) partial.clone();
				newPartial.add(new Pair(remainder[0], remainder[i]));
				r1 = Arrays.copyOfRange(remainder, 1, i);
				r2 = Arrays.copyOfRange(remainder, i + 1, remainder.length);
				findBestPairings((Player[]) ArrayUtils.addAll(r1, r2), newPartial);
				
			}	
		}
		
	}

	
	private void findBestDynamicPairings() {

		Pair[] p;
		float dynamicScore;
		
		for (Pair[] pairing : bestPairings) {
			
			for (boolean[] s : flipSequences) {
				p = pairing.clone();
				for (int i = 0; i < p.length; i++) {
					if (s[i]) p[i] = pairing[i].getFlipped();
				}
				
				dynamicScore = dynamicallyScorePairing(p);
				if (dynamicScore < bestDynamicScore) {
					bestDynamicScore = dynamicScore;
					bestColoredPairings = new ArrayList<Pair[]>();
					bestColoredPairings.add(p);
				} else if (bestDynamicScore == dynamicScore) {
					bestColoredPairings.add(p);
//					if (bestColoredPairings.size() > 16) {
//						Pair[] temp = bestColoredPairings.get(random.nextInt(17));
//						bestColoredPairings = new ArrayList<Pair[]>();
//						bestColoredPairings.add(temp);
//					}

				}
			}
		}	
	}

	
//	public void permutePairings() {
//		
//		int i = 0;
//		Pair[] p;
//		
//		for (Pair[] pairing : permuteBasePairings(players, null)) {
//
//			for (boolean[] s : permuteFlipSequences(new boolean[pairing.length], pairing.length)) {
//				p = pairing.clone();
//				for (int j = 0; j < p.length; j++) {
//					if (s[j]) {
//						p[j] = pairing[j].getFlipped();
//					} else {
//						p[j] = pairing[j];
//					}
//				}
//				pairings[i++] = p;
//			}
//			
//		}
//		
//		
//	}
		
//	@SuppressWarnings("unchecked")
//	private static List<Pair[]> permuteBasePairings(Player[] remainder, ArrayList<Pair> partial) {
//		
//		ArrayList<Pair[]> pairings = new ArrayList<Pair[]>();
//		ArrayList<Pair> newPartial;
//		
//		if (partial == null) {
//			partial = new ArrayList<Pair>();
//		}
//		
//		if (remainder.length == 0) {
//			pairings.add(partial.toArray(new Pair[0]));
//		} else {
//			
//			Player[] r1, r2;
//			
//			for (int i = 1; i < remainder.length; i++) {
//			
//				newPartial = (ArrayList<Pair>) partial.clone();
//				newPartial.add(new Pair(remainder[0], remainder[i]));
//				r1 = Arrays.copyOfRange(remainder, 1, i);
//				r2 = Arrays.copyOfRange(remainder, i + 1, remainder.length);
//				
//				for (Pair[] pairing : permuteBasePairings((Player[]) ArrayUtils.addAll(r1, r2), newPartial)) {
//					pairings.add(pairing);
//				}		
//			}	
//		}
//		
//		return pairings;
//		
//	}
	
	
	private static List<boolean[]> permuteFlipSequences(boolean[] sequence, int i) {
		
		List<boolean[]> rtn;
		
		if (i == 0) {
			
			rtn = new ArrayList<boolean[]>(MathUtils.pow(2, Collections.frequency(Arrays.asList(sequence), false)));
			rtn.add(sequence);
			
		} else {
			
			boolean[] sequence0 = Arrays.copyOf(sequence, sequence.length);
			sequence0[sequence.length - i] = false;
			rtn = permuteFlipSequences(sequence0, i - 1);
			
			boolean[] sequence1 = Arrays.copyOf(sequence, sequence.length);
			sequence1[sequence.length - i] = true;
			
			rtn.addAll(permuteFlipSequences(sequence1, i - 1));
			
		}
		
		return rtn;
	}
	
//	public void findBestPairing() {
//	
//		shuffle(pairings);
//		shuffle(pairings);
//		shuffle(pairings);
//		
//		float bestScore = Float.POSITIVE_INFINITY;
//		
//		for (Pair[] p : pairings) {
//			
//			float score = scorePairing(p);
//			if (score < bestScore) {
//				bestScore = score;
//				bestPairing = p;
//			}		
//		}
//	}
	
	private static void shuffle(Player[] array) {
		int l = array.length;
		for (int i = 0; i < l; i++) {
			int index = random.nextInt(l - i) + i;
			Player temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}
	
	private static float staticallyScorePairing(Pair[] pairing) {
		
		float rtn = 0;
		
		for (Pair p : pairing) {
			rtn += p.getStaticBadness();
			if (rtn == Float.POSITIVE_INFINITY) break;
		}
		
		return rtn;
	}
	
	private static float dynamicallyScorePairing(Pair[] pairing) {
		
		float rtn = 0;
		
		for (Pair p : pairing) {
			rtn += p.getDynamicBadness();
			if (rtn == Float.POSITIVE_INFINITY) break;
		}
		
		return rtn;		
		
	}
	
	public Element toXML(Document doc) {
		
		Element e = doc.createElement(TOURNAMENT);
		for (Player p : players) {
			e.appendChild(p.toXML(doc));
		}
		return e;
		
	}
	
	public void printBestPairing() {
		printPairing(bestPairing);
	}
	
	public static void printPairing(Pair[] pairing) {
		System.out.println(" White\t\tBlack");
		System.out.println("-----------------------");
		
		for (Pair p : pairing) {
			System.out.println(" " + p);
		}
		
		System.out.println();

	}
	
	public PdfPTable generateBestPairingTable() {
		
		PdfPTable table = new PdfPTable(2);
		
		Phrase phrase = new Phrase("WHITE");
		PdfPCell cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);
		phrase = new Phrase("BLACK");
		cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);
		
		for (Pair p : bestPairing) {
			table.addCell(p.getWhite().getName());
			table.addCell(p.getBlack().getName());
		}
		
		return table;
	}
	
	public void printPlayerInfo() {
		
		System.out.println(" Player\t\tScore\tColors\tOpponents");
		System.out.println("------------------------------------------");
		
		for (Player p : players) {
			System.out.println(" " + p.infoToString());
		}

	}
	
	private static final float[] relativeSizes = {32, 24, 32, 64};
	public PdfPTable generatePlayerInfoTable() {
		
		PdfPTable table = new PdfPTable(relativeSizes);
		
		Phrase phrase = new Phrase("PLAYER");
		PdfPCell cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);
		phrase = new Phrase("SCORE");
		cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);
		phrase = new Phrase("COLORS");
		cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);
		phrase = new Phrase("OPPONENTS");
		cell = new PdfPCell(phrase);
		cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
		table.addCell(cell);

		for (Player p : players) {
			
			table.addCell(p.getName());
			table.addCell(Float.toString(p.getScore()));
			table.addCell(p.getColorList());
			table.addCell(p.getOpponentList());
			
		}
		
		return table;
	}
	
}
