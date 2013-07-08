package estabrook.chessclub.chesspairs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.util.MathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Player {
	
	public static final Player UNPAIRED = new Player("UNPAIRED");

	private static final String PLAYER = "player";
	private static final String NAME = "name";
	private static final String GAME = "game";
		
	private final String name;
	private final List<GameRecord> games;
	private float score;
	private float whiteBadness;
	private float blackBadness;
	
	public Player(Element playerXML) {
				
		name = playerXML.getAttribute(NAME);
		
		NodeList gamesXML = playerXML.getElementsByTagName(GAME);
		games = new ArrayList<GameRecord>(gamesXML.getLength());
		for (int i = 0; i < gamesXML.getLength(); i++) {
			games.add(new GameRecord((Element) gamesXML.item(i)));
		}
		calculateScore();
		calculateColorBadnesses();

	}
	
	public Player(String name) {
		this.name = name;
		games = new ArrayList<GameRecord>();
		calculateScore();
		calculateColorBadnesses();
	}
	
	public String getName() {
		return name;
	}
	
	public float getScore() {
		return score;
	}
	
	public void calculateScore() {
		score = 0;
		for (GameRecord g : games) {
			score += g.getOutcome();
		}
	}
	
	public void calculateColorBadnesses() {
		
		whiteBadness = 0;
		blackBadness = 0;
		
		if (games.size() > 0) {
			
			List<Color> previousColors = new ArrayList<Color>(games.size());
			for (GameRecord g : games) {
				previousColors.add(g.getColor());
			}
			
			whiteBadness += MathUtils.pow(Collections.frequency(previousColors, Color.WHITE) + 1 - 
					Collections.frequency(previousColors, Color.BLACK) - 0, 2);
			
			whiteBadness += Color.WHITE == previousColors.get(previousColors.size() - 1) ? 1 : 0;
			
			blackBadness += MathUtils.pow(Collections.frequency(previousColors, Color.WHITE) + 0 - 
					Collections.frequency(previousColors, Color.BLACK) - 1, 2);
			
			blackBadness += Color.BLACK == previousColors.get(previousColors.size() - 1) ? 1 : 0;

		}
				
	}
	
	public float getColorBadness(Color c) {
		
		switch (c) {
			case WHITE: return whiteBadness;
			case BLACK: return blackBadness;
			default: return 0;
		}
		
	}
	
	public List<GameRecord> getGames() {
		return games;
	}
	
	public boolean hasPlayed(Player p) {
		
		for (GameRecord g : games) {
			if (p.name.equals(g.getOpponent())) return true;
		}
		
		return false;
	}
	
	public void addGame(String opponent, Color color, float outcome) {
		games.add(new GameRecord(opponent, outcome, color));
		calculateScore();
		calculateColorBadnesses();
	}
	
	public void removeGame() {
		games.remove(games.size() - 1);
		calculateScore();
		calculateColorBadnesses();
	}
	
	public Element toXML(Document doc) {
		
		Element e = doc.createElement(PLAYER);
		e.setAttribute(NAME, name);
		for (GameRecord g : games) {
			e.appendChild(g.toXML(doc));
		}
		return e;

	}
	
	public String infoToString() {
		
		StringBuilder sb = new StringBuilder(name + "\t" + score + "\t[");
		
		if (games.size() > 0) {
			for (GameRecord g : games) {
				sb.append(g.getColor().toString().charAt(0) + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
			
			sb.append("]\t[");
			for (GameRecord g : games) {
				sb.append(g.getOpponent() + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append("]");
		} else {
			sb.append("]\t[]");
		}
		
		return sb.toString();
		
	}
	
	public String toString() {
		return name;
	}
	
	public String getColorList() {
		StringBuilder sb = new StringBuilder();
		if (games.size() > 0) {
			for (GameRecord g : games) {
				sb.append(g.getColor().toString().charAt(0) + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}
	
	public String getOpponentList() {
		StringBuilder sb = new StringBuilder();
		if (games.size() > 0) {
			for (GameRecord g : games) {
				sb.append(g.getOpponent().toString() + ", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.toString();
	}
	
	public boolean equals(Object other) {
		return this.name.equals(((Player) other).getName());
	}
	
}
