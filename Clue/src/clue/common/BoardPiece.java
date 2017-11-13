package clue.common;

public abstract class BoardPiece {
	// Member variables
	protected String displayName;
	protected int locationX;
	protected int locationY;
	
	// Constructor
	public BoardPiece(String diplayName) {
		// Initialize member variables
		setDisplayName(displayName);
		setLocationX(-1);
		setLocationY(-1);
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
	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}
	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}
}
