package estabrook.chessclub.chesspairs;

public class Game {

	private final Player white;
	private final boolean whiteNoCount;
	private final Player black;
	private final boolean blackNoCount;
	private final float outcome;
		
	public Game(Player white, boolean whiteNoCount, Player black, boolean blackNoCount, float outcome) {
		this.white = white;
		this.whiteNoCount = whiteNoCount;
		this.black = black;
		this.blackNoCount = blackNoCount;
		this.outcome = outcome;
	}
	
	public void storeAsRecord() {
		
		if (whiteNoCount || white.equals(Player.UNPAIRED)) {
			white.addGame(black.getName(), Color.NA, 0);
		} else {
			white.addGame(black.getName(), Color.WHITE, outcome);
		}
		
		if (blackNoCount || black.equals(Player.UNPAIRED)) {
			black.addGame(white.getName(), Color.NA, 0);
		} else {
			black.addGame(white.getName(), Color.BLACK, 1 - outcome);
		}
		
	}
	
	public void undoStoreAsRecord() {
		white.removeGame();
		black.removeGame();
	}
	
}
