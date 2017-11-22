package clue.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import clue.common.GameBoard.MoveDirection;

public abstract class BoardLocation {
	// Enumeration
	public enum LocationType{
		ROOM(0,"Room"),
		HALLWAY(1,"Hallway");	
		
		private final int id;
		private final String defaultName;
		LocationType(int id, String defaultName){
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
	protected LocationType locationType;
	protected String displayName;
	protected int locationX;
	protected int locationY;
	protected int capacity;
	protected Set<Weapon> weapons;
	protected Set<Token> tokens;
	protected Map<MoveDirection,BoardLocation> neighbors;
	
	// Constructor
	public BoardLocation(LocationType locationType, String displayName,int capacity) {
		// Initialize member variables
		this.locationType = locationType;
		this.displayName = displayName;
		this.locationX = -1;
		this.locationY = -1;
		this.capacity = capacity;
		
		// Initialize data structures
		weapons = new HashSet<Weapon>();
		tokens = new HashSet<Token>();
		neighbors = new HashMap<MoveDirection,BoardLocation>();
	}
	
	// Get methods
	public LocationType getLocationType() {
		return locationType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public int getLocationX() {
		return locationX;
	}
	public int getLocationY() {
		return locationY;
	}
	public boolean isFull() {
		return (tokens.size() >= capacity);
	}
	public Set<Token> getTokens(){
		// Shallow copy
		return new HashSet<Token>(tokens);
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
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	// Data structure modifiers
	public boolean addToken(Token boardPiece) {
		if(isFull()) {
			// Already full
			System.err.println("Cannot add token to " + displayName + ", location is already full");
			return false;
		}else {
			// Try adding the board piece
			boolean addSuccess = tokens.add(boardPiece);
			if(addSuccess) {
				// Update the board piece's location
				boardPiece.setLocationXY(this.locationX,this.locationY);
			}
			return addSuccess;
		}
	}
	public boolean addWeapon(Weapon boardPiece) {
		boolean addSuccess = weapons.add(boardPiece);
		if(addSuccess) {
			// Update the board piece's location
			boardPiece.setLocationXY(this.locationX,this.locationY);
		}
		return addSuccess;
	}
    public boolean removeToken(Token boardPiece) {
    	return tokens.remove(boardPiece);
    }
    public boolean removeWeapon(Weapon boardPiece) {
    	return weapons.remove(boardPiece);
    }
    public void addNeighbor(MoveDirection moveDirection, BoardLocation boardLocation) {
    	neighbors.put(moveDirection, boardLocation);
    }
    
    // Get the neighbors that are not already full
    public Map<MoveDirection,BoardLocation> getValidMoves() {
    	Map<MoveDirection,BoardLocation> validMoves = new HashMap<MoveDirection,BoardLocation>();
    	for(MoveDirection m : neighbors.keySet()) {
    		if(!neighbors.get(m).isFull()) {
    			validMoves.put(m, neighbors.get(m));
    		}
    	}
    	return validMoves;
    }
}
