package clue.common;

import java.util.HashMap;
import java.util.Map;

import clue.common.Room.RoomId;
import clue.common.Token.TokenId;
import clue.common.Weapon.WeaponId;

public class GameBoard {
	// Member variables
	BoardLocation[][] grid;
	Map<TokenId,Token> tokenMap;
	Map<RoomId,Room> roomMap;
	Map<WeaponId,Weapon> weaponMap;
	
	// Constructor to create a blank initialized game board
	public GameBoard() {
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
	}
	
	// Method to initialize default token locations
	public void initializeDefaultTokenLocations() {
		// Add tokens to board
		grid[0][3].addOccupant(tokenMap.get(TokenId.MISS_SCARLET));
		grid[1][4].addOccupant(tokenMap.get(TokenId.COLONEL_MUSTARD));
		grid[1][0].addOccupant(tokenMap.get(TokenId.PROFESSOR_PLUM));
		grid[3][0].addOccupant(tokenMap.get(TokenId.MRS_PEACOCK));
		grid[4][1].addOccupant(tokenMap.get(TokenId.MR_GREEN));
		grid[4][3].addOccupant(tokenMap.get(TokenId.MRS_WHITE));
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
				if((i % 0 == 0) && (j % 0 != 0) || (i % 0 != 0) && (j % 0 == 0)){
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
					grid[i][j].addNeighbor(grid[i-1][j]);
				}
				// Down neighbor
				if(i+1 < 5 && grid[i+1][j] != null) {
					grid[i][j].addNeighbor(grid[i+1][j]);
				}
				// Left neighbor
				if(j-1 >= 0 && grid[i][j-1] != null) {
					grid[i][j].addNeighbor(grid[i][j-1]);
				}
				// Right neighbor
				if(j+1 < 5 && grid[i][j+1] != null) {
					grid[i][j].addNeighbor(grid[i][j+1]);
				}					
			}
		}
		
		// Add diagonal neighbors
		grid[0][0].addNeighbor(grid[4][4]);
		grid[0][4].addNeighbor(grid[4][0]);
		grid[4][0].addNeighbor(grid[0][4]);
		grid[4][4].addNeighbor(grid[0][0]);
	}
	
	// Helper function to add a board location to the grid and set its coordinates
	private void addBoardLocationToGrid(int x, int y, BoardLocation boardLocation) {
		grid[x][y] = boardLocation;
		boardLocation.setLocationX(x);
		boardLocation.setLocationY(y);
	}
}
