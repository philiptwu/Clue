package clue.common;

public class Token extends BoardPiece {
	// Enumeration
	public enum TokenId{
		MISS_SCARLET(0,"Miss Scarlet"),
		COLONEL_MUSTARD(1,"Colonel Mustard"),
		MRS_WHITE(2,"Mrs. White"),
		MR_GREEN(3,"Mr. Green"),
		MRS_PEACOCK(4,"Mrs. Peacock"),
		PROFESSOR_PLUM(5,"Professor Plum");
		
		private final int id;
		private final String defaultName;
		TokenId(int id, String defaultName){
			this.id = id;
			this.defaultName = defaultName;
		}
		public int getValue() {
			return id;
		}
		public String getDefaultName() {
			return defaultName;
		}
	}
		
	// Member variables
	protected TokenId tokenId;
	protected boolean previousValid;
	protected int previousX;
	protected int previousY;
	
	// Constructor
	public Token(TokenId tokenId) {
		// Parent constructor
		super(tokenId.getDefaultName());
				
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
