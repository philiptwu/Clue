package clue.common;

import java.util.HashSet;
import java.util.Set;

public abstract class BoardLocation {
	// Member variables
	protected String displayName;
	protected int locationX;
	protected int locationY;
	protected Set<BoardPiece> occupants;
	protected Set<BoardLocation> neighbors;
	
	// Constructor
	public BoardLocation(String diplayName, int locationX, int locationY) {
		// Initialize member variables
		setDisplayName(displayName);
		setLocationX(locationX);
		setLocationY(locationY);
		
		// Initialize data structures
		occupants = new HashSet<BoardPiece>();
		neighbors = new HashSet<BoardLocation>();
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
	public Set<BoardPiece> getOccupants(){
		// Shallow copy
		return new HashSet<BoardPiece>(occupants);
	}
	public Set<BoardLocation> getNeighbors(){
		// Shallow copy
		return new HashSet<BoardLocation>(neighbors);
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

	// Data structure modifiers
	public boolean addOccupant(BoardPiece boardPiece) {
		return occupants.add(boardPiece);
	}
    public boolean removeOccupant(BoardPiece boardPiece) {
    	return occupants.remove(boardPiece);
    }
    public void addNeighbor(BoardLocation boardLocation) {
    	this.neighbors.add(boardLocation);
    }
}
