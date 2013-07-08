package estabrook.chessclub.chesspairs;

public enum Color {
	WHITE, BLACK, NA;
	
	public static Color parseColor(int i) {
		switch(i) {
		case 0: return WHITE;
		case 1: return BLACK;
		default: return NA;
		}
	}
	
	public static int toInteger(Color c) {
		switch(c) {
		case WHITE: return 0;
		case BLACK: return 1;
		default: return -1;
		}
	}
	
	public String toString() {
		String s = super.toString();
		return s.charAt(0) + s.substring(1).toLowerCase();
	}
	
}
