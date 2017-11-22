package clue.common;

public abstract class BoardPiece {
	// Member variables
	protected String displayName;
	protected int locationX;
	protected int locationY;
	
	// Constructor
	public BoardPiece(String displayName) {
		// Initialize member variables
		this.displayName = displayName;
		this.locationX = -1;
		this.locationY = -1;
	}
		
	// Get methods
	public String getDisplayName() {
		return displayName;
	}
	public int getLocationX() {
		return locationX;
	}
	public int getLocationY() {
		return locationY;
	}
	
	// Set methods
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setLocationXY(int locationX, int locationY) {
		this.locationX = locationX;
		this.locationY = locationY;
	}
}
