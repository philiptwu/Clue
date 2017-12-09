package clue.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import clue.common.BoardLocation.LocationType;
import clue.common.Room.RoomId;
import clue.common.Token.TokenId;
import clue.common.Weapon.WeaponId;

public class GameBoard {
	public enum MoveDirection{
		NORTH(0,"North"),
		SOUTH(1,"South"),
		WEST(2,"West"),
		EAST(3,"East"),
		SECRET(4,"Secret");
		
		private final int id;
		private final String displayName;
		MoveDirection(int id, String displayName){
			this.id = id;
			this.displayName = displayName;
		}
		public int getValue() {
			return id;
		}
		public String toString() {
			return displayName;
		}
	}
	
	// Member variables
	protected BoardLocation[][] grid;
	protected Map<TokenId,Token> tokenMap;
	protected Map<RoomId,Room> roomMap;
	protected Map<WeaponId,Weapon> weaponMap;
	
	// Constructor to create a blank initialized game board
	public GameBoard(Random random) {
		// Initialize data structures
		grid = new BoardLocation[5][5];
 		tokenMap = new HashMap<TokenId,Token>();
 		roomMap = new HashMap<RoomId,Room>();
		weaponMap = new HashMap<WeaponId,Weapon>();
 		
		// Create rooms and hallways, place them on grid, connect them,
		// and add rooms to the room map.  This is always the same for
		// every single game board
		initializeGridLocations();
		
		// Create tokens but don't place them in game board
		Token missScarlet = new Token(TokenId.MISS_SCARLET);
		Token colonelMustard = new Token(TokenId.COLONEL_MUSTARD);
		Token professorPlum = new Token(TokenId.PROFESSOR_PLUM);
		Token mrsPeacock = new Token(TokenId.MRS_PEACOCK);
		Token mrGreen = new Token(TokenId.MR_GREEN);
		Token mrsWhite = new Token(TokenId.MRS_WHITE);
		
		// Add tokens to the token map for easy lookup later
		tokenMap.put(missScarlet.getTokenId(), missScarlet);
		tokenMap.put(colonelMustard.getTokenId(), colonelMustard);
		tokenMap.put(professorPlum.getTokenId(),  professorPlum);
		tokenMap.put(mrsPeacock.getTokenId(), mrsPeacock);
		tokenMap.put(mrGreen.getTokenId(), mrGreen);
		tokenMap.put(mrsWhite.getTokenId(),  mrsWhite);

		// Place tokens in board
		initializeDefaultTokenLocations();
		
		// Create weapons but don't place them in game board
		Weapon candlestick = new Weapon(WeaponId.CANDLESTICK);
		Weapon knife = new Weapon(WeaponId.KNIFE);
		Weapon leadPipe = new Weapon(WeaponId.LEAD_PIPE);
		Weapon revolver = new Weapon(WeaponId.REVOLVER);
		Weapon rope = new Weapon(WeaponId.ROPE);
		Weapon wrench = new Weapon(WeaponId.WRENCH);
		
		// Add weapons to weapon map for easy lookup later
		weaponMap.put(candlestick.getWeaponId(), candlestick);
		weaponMap.put(knife.getWeaponId(), knife);
		weaponMap.put(leadPipe.getWeaponId(), leadPipe);
		weaponMap.put(revolver.getWeaponId(), revolver);
		weaponMap.put(rope.getWeaponId(), rope);
		weaponMap.put(wrench.getWeaponId(), wrench);
		
		// Randomly place weapons on board
		initializeRandomWeaponLocations(random);
	}
	
	// Reverse lookup move direction by value
	public static MoveDirection getMoveDirectionByValue(int id) {
		for(MoveDirection md : MoveDirection.values()) {
			if(id == md.getValue()) {
				return md;
			}
		}
		return null;
	}
	
	// Gets all the tokens
	public List<Token> getTokens(){
		return new ArrayList<Token>(tokenMap.values());
	}
	
	// Get all the rooms
	public List<Room> getRooms(){
		return new ArrayList<Room>(roomMap.values());
	}
	
	// Get all the weapons
	public List<Weapon> getWeapons(){
		return new ArrayList<Weapon>(weaponMap.values());
	}
	
	public BoardLocation getBoardLocation(int x, int y) {
		return grid[x][y];
	}
	
	// Method to initialize default token locations
	public void initializeDefaultTokenLocations() {
		// Add tokens to board
		grid[0][3].addToken(tokenMap.get(TokenId.MISS_SCARLET));
		grid[1][4].addToken(tokenMap.get(TokenId.COLONEL_MUSTARD));
		grid[1][0].addToken(tokenMap.get(TokenId.PROFESSOR_PLUM));
		grid[3][0].addToken(tokenMap.get(TokenId.MRS_PEACOCK));
		grid[4][1].addToken(tokenMap.get(TokenId.MR_GREEN));
		grid[4][3].addToken(tokenMap.get(TokenId.MRS_WHITE));
	}
	
	// Method to randomize initial weapon locations
	public void initializeRandomWeaponLocations(Random random) {
		List<Room> rooms = new ArrayList<Room>(roomMap.values());
		for (Weapon w : weaponMap.values()) {
			// Randomly choose a room to place each weapon
			rooms.get(random.nextInt(rooms.size())).addWeapon(w);
		}
	}
	
	// Get the valid move directions for a particular token ID
	public List<MoveDirection> getMoveDirections(Token t){
		// Get token's current position's valid neighbors
		return new ArrayList<MoveDirection>(grid[t.getLocationX()][t.getLocationY()].getValidMoves().keySet());		
	}
	
	// Move the token
	public void moveToken(Token t, MoveDirection m) {
		Map<MoveDirection, BoardLocation> validMoves = grid[t.getLocationX()][t.getLocationY()].getValidMoves();
		moveToken(t,validMoves.get(m));
	}
	public void moveToken(Token t, BoardLocation newLocation) {
		// Remove token from old location
		BoardLocation currLocation = grid[t.getLocationX()][t.getLocationY()];
		currLocation.removeToken(t);
			
		// Add token to new location
		newLocation.addToken(t);
	}
	
	// Move the weapon
	public void moveWeapon(Weapon w, BoardLocation newLocation) {
		// Remove token from old location
		BoardLocation currLocation = grid[w.getLocationX()][w.getLocationY()];
		currLocation.removeWeapon(w);
			
		// Add token to new location
		newLocation.addWeapon(w);
	}
	
	// Move the token to the closest room
	public void moveTokenToClosestRoom(Token t) {
		// Get token's current location
		BoardLocation currLocation = grid[t.getLocationX()][t.getLocationY()];
		
		// Check to see if it is a room
		if(currLocation.getLocationType() != LocationType.ROOM) {
			// Get neighbor locations
			Map<MoveDirection,BoardLocation> validMoves = currLocation.getValidMoves();			
			for(MoveDirection m : validMoves.keySet()) {
				// Move to first available location
				moveToken(t, validMoves.get(m));
				return;
			}
		}
	}
	
	// Whether or not a token is in a room
	public boolean getIsTokenInRoom(Token t) {
		// Get token's current location
		BoardLocation currLocation = grid[t.getLocationX()][t.getLocationY()];
		
		// Check to see whether the current location is a room
		return (currLocation.getLocationType() == LocationType.ROOM);
	}
	
	// Method to initialize a game grid locations
	public void initializeGridLocations() {
		// Create rooms
		Room study = new Room(RoomId.STUDY);
		Room hall = new Room(RoomId.HALL);
		Room lounge = new Room(RoomId.LOUNGE);
		Room library = new Room(RoomId.LIBRARY);
		Room billiardRoom = new Room(RoomId.BILLIARD_ROOM);
		Room diningRoom = new Room(RoomId.DINING_ROOM);
		Room conservatory = new Room(RoomId.CONSERVATORY);
		Room ballroom = new Room(RoomId.BALLROOM);
		Room kitchen = new Room(RoomId.KITCHEN);
		
		// Add rooms to board
		addBoardLocationToGrid(0,0,study);
		addBoardLocationToGrid(0,2,hall);
		addBoardLocationToGrid(0,4,lounge);
		addBoardLocationToGrid(2,0,library);
		addBoardLocationToGrid(2,2,billiardRoom);
		addBoardLocationToGrid(2,4,diningRoom);
		addBoardLocationToGrid(4,0,conservatory);
		addBoardLocationToGrid(4,2,ballroom);
		addBoardLocationToGrid(4,4,kitchen);
		
		// Add rooms to room map for easy lookup later
		roomMap.put(study.getRoomId(), study);
		roomMap.put(hall.getRoomId(), hall);
		roomMap.put(lounge.getRoomId(), lounge);
		roomMap.put(library.getRoomId(), library);
		roomMap.put(billiardRoom.getRoomId(), billiardRoom);
		roomMap.put(diningRoom.getRoomId(), diningRoom);
		roomMap.put(conservatory.getRoomId(), conservatory);
		roomMap.put(ballroom.getRoomId(), ballroom);
		roomMap.put(kitchen.getRoomId(), kitchen);
		
		// Add hallways
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				if((i % 2 == 0) && (j % 2 != 0) || (i % 2 != 0) && (j % 2 == 0)){
					addBoardLocationToGrid(i,j,new Hallway());
				}
			}
		}
		
		// Set adjacent connections
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				// Skip if blank space
				if(grid[i][j] == null) {
					continue;
				}
				
				// Add adjacent neighbors (as applicable)
				// Up neighbor
				if(i-1 >= 0 && grid[i-1][j] != null) {
					grid[i][j].addNeighbor(MoveDirection.NORTH,grid[i-1][j]);
				}
				// Down neighbor
				if(i+1 < 5 && grid[i+1][j] != null) {
					grid[i][j].addNeighbor(MoveDirection.SOUTH,grid[i+1][j]);
				}
				// Left neighbor
				if(j-1 >= 0 && grid[i][j-1] != null) {
					grid[i][j].addNeighbor(MoveDirection.WEST,grid[i][j-1]);
				}
				// Right neighbor
				if(j+1 < 5 && grid[i][j+1] != null) {
					grid[i][j].addNeighbor(MoveDirection.EAST,grid[i][j+1]);
				}					
			}
		}
		
		// Add diagonal neighbors
		grid[0][0].addNeighbor(MoveDirection.SECRET,grid[4][4]);
		grid[0][4].addNeighbor(MoveDirection.SECRET,grid[4][0]);
		grid[4][0].addNeighbor(MoveDirection.SECRET,grid[0][4]);
		grid[4][4].addNeighbor(MoveDirection.SECRET,grid[0][0]);
	}
	
	// Helper function to add a board location to the grid and set its coordinates
	private void addBoardLocationToGrid(int x, int y, BoardLocation boardLocation) {
		grid[x][y] = boardLocation;
		boardLocation.setLocationX(x);
		boardLocation.setLocationY(y);
	}
}
