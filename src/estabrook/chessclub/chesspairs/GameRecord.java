package estabrook.chessclub.chesspairs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GameRecord {
		
	private static final String GAME = "game";
	private static final String OPPONENT = "opponent";
	private static final String OUTCOME = "outcome";
	private static final String COLOR = "color";
	
	private final String opponent;
	private final float outcome;
	private final Color color;
	
	public GameRecord(Element gameXML) {
		opponent = gameXML.getAttribute(OPPONENT);
		outcome = Float.parseFloat(gameXML.getAttribute(OUTCOME));
		color = Color.parseColor(Integer.parseInt(gameXML.getAttribute(COLOR)));
	}
	
	public GameRecord(String opponent, float outcome, Color color) {
		this.opponent = opponent;
		this.outcome = outcome;
		this.color = color;
	}
	
	public Element toXML(Document doc) {
		
		Element e = doc.createElement(GAME);
		e.setAttribute(OPPONENT, opponent);
		e.setAttribute(OUTCOME, Float.toString(outcome));
		e.setAttribute(COLOR, Integer.toString(Color.toInteger(color)));
		return e;
		
	}
	
	public String getOpponent() {
		return opponent;
	}
	
	public float getOutcome() {
		return outcome;
	}
	
	public Color getColor() {
		return color;
	}
	
}
