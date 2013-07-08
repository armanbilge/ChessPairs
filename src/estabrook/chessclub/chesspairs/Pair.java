package estabrook.chessclub.chesspairs;

public class Pair {

	private Player white;
	private Player black;
	private Pair flipped;
	private float opponentBadness = 0;
	private float scoreBadness = 0;
	private float colorBadness = 0;

	// Badness score factors; may need adjustment
	private static final int OPPONENT_FACTOR = 8192;
	private static final int SCORE_FACTOR = 512;
	private static final int COLOR_FACTOR = 1;

	
	public Pair(Player white, Player black) {
		this.white = white;
		this.black = black;
		this.flipped = new Pair(this);
		calculateBadness();
	}
	
	public Pair(Pair p) {
		this.white = p.black;
		this.black = p.white;
		this.flipped = p;
		calculateBadness();
	}
	
	private void calculateBadness() {
		// Same players shouldn't play each other again
		if (white.hasPlayed(black) || black.hasPlayed(white))
			opponentBadness = OPPONENT_FACTOR;
	
		if (opponentBadness < Float.POSITIVE_INFINITY) {
			// Players should have similar scores
			scoreBadness = (float) (SCORE_FACTOR
				* Math.pow(white.getScore() - black.getScore(), 2));
			// Try to optimize colors
			colorBadness = COLOR_FACTOR
				* (white.getColorBadness(Color.WHITE) +
						black.getColorBadness(Color.BLACK));
		}
	}
	
	public Player getWhite() {
		return white;
	}
	
	public Player getBlack() {
		return black;
	}
	
	public Pair getFlipped() {
		return flipped;
	}
		
	public float getStaticBadness() {
		return opponentBadness + scoreBadness;
	}
	
	public float getDynamicBadness() {
		return colorBadness;
	}
	
	public String toString() {
		return white.getName() + "\t" + black.getName();
	}
	
}
