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
	protected boolean available;
	protected TokenId tokenId;
	protected boolean previousValid;
	protected int previousX;
	protected int previousY;
	protected boolean justTeleported;
	protected boolean movedSinceSuggested;
	
	// Constructor
	public Token(TokenId tokenId) {
		// Parent constructor
		super(tokenId.getDefaultName());
				
		// Set member variables
		this.available = true;
		this.tokenId = tokenId;
		this.previousValid = false;
		this.previousX = -1;
		this.previousY = -1;
		this.justTeleported = false;
		this.movedSinceSuggested = true;
	}
	
	// Reverse lookup token ID by value
	public static TokenId getTokenIdByValue(int id) {
		for(TokenId tid : TokenId.values()) {
			if(id == tid.getValue()) {
				return tid;
			}
		}
		return null;
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
	public boolean getAvailable() {
		return available;
	}
	
	public void recordSuggestion() {
		movedSinceSuggested = false;
	}
	
	// Set methods
	@Override
	public void setLocationXY(int locationX, int locationY) {
		// Set current location as previous location
		previousX = this.locationX;
		previousY = this.locationY;
		previousValid = (previousX >= 0 && previousY >= 0);

		// Set the new location
		this.locationX = locationX;
		this.locationY = locationY;

		movedSinceSuggested = true;
	}
	
	public void setTeleportedLocationXY(int locationX, int locationY) {
		// Set the new location
		previousValid = false;
		this.locationX = locationX;
		this.locationY = locationY;
		justTeleported = true;
		movedSinceSuggested = true;
	}
	
	public void clearJustTeleported() {
		justTeleported = false;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
