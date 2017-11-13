package clue.common;

public class Token extends BoardPiece {
	// Enumeration
	public enum TokenId{
		MISS_SCARLET(0),
		COLONEL_MUSTARD(1),
		MRS_WHITE(2),
		MR_GREEN(3),
		MRS_PEACOCK(4),
		PROFESSOR_PLUM(5);
		
		private final int id;
		TokenId(int id){
			this.id = id;
		}
		public int getValue() {
			return id;
		}
	}
	
	// Member variables
	protected TokenId tokenId;
	protected boolean previousValid;
	protected int previousX;
	protected int previousY;
	
	// Constructor
	public Token(TokenId tokenId, String displayName) {
		// Parent constructor
		super(displayName);
				
		// Set member variables
		this.tokenId = tokenId;
		this.previousValid = false;
		this.previousX = -1;
		this.previousY = -1;
	}
	
	// Get methods
	public TokenId getTokenId() {
		return tokenId;
	}
	public boolean getPreviousValid() {
		return previousValid;
	}
	public int getPreviousX() {
		return previousX;
	}
	public int getPreviousY() {
		return previousY;
	}
	
	// Set methods
	public void setPreviousValid(boolean previousValid) {
		this.previousValid = previousValid;
	}
	public void setPreviousX(int previousX) {
		this.previousX = previousX;
	}
	public void setPreviousY(int previousY) {
		this.previousY = previousY;
	}
}
