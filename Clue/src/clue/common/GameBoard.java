package clue.common;

import java.util.HashMap;
import java.util.Map;

import clue.common.Room.RoomId;
import clue.common.Token.TokenId;

public class GameBoard {
	// Member variables
	BoardLocation[][] grid;
	Map<TokenId,Token> tokenMap;
	
	// Constructor to create a blank initialized game board
	public GameBoard() {
		// Initialize data structures
		grid = new BoardLocation[5][5];
 		tokenMap = new HashMap<TokenId,Token>();
		
		// Initialize the game grid
		initializeGrid();
		
		// Create tokens
		Token missScarlet = new Token(TokenId.MISS_SCARLET, "Miss Scarlet");
		Token colonelMustard = new Token(TokenId.COLONEL_MUSTARD, "Colonel Mustard");
		Token professorPlum = new Token(TokenId.PROFESSOR_PLUM, "Professor Plum");
		Token mrsPeacock = new Token(TokenId.MRS_PEACOCK, "Mrs. Peacock");
		Token mrGreen = new Token(TokenId.MR_GREEN, "Mr. Green");
		Token mrsWhite = new Token(TokenId.MRS_WHITE, "Mrs. White");
		
		// Add tokens to board
		grid[0][3].addOccupant(missScarlet);
		grid[1][4].addOccupant(colonelMustard);
		grid[1][0].addOccupant(professorPlum);
		grid[3][0].addOccupant(mrsPeacock);
		grid[4][1].addOccupant(mrGreen);
		grid[4][3].addOccupant(mrsWhite);
		
		// Add tokens to the token map
		tokenMap.put(missScarlet.getTokenId(), missScarlet);
		tokenMap.put(colonelMustard.getTokenId(), colonelMustard);
		tokenMap.put(professorPlum.getTokenId(),  professorPlum);
		tokenMap.put(mrsPeacock.getTokenId(), mrsPeacock);
		tokenMap.put(mrGreen.getTokenId(), mrGreen);
		tokenMap.put(mrsWhite.getTokenId(),  mrsWhite);
	}
			
	// Method to initialize a game grid
	public void initializeGrid() {
		// Add rooms
		addBoardLocationToGrid(0,0,new Room(RoomId.STUDY,"Study"));
		addBoardLocationToGrid(0,2,new Room(RoomId.HALL,"Hall"));
		addBoardLocationToGrid(0,4,new Room(RoomId.LOUNGE,"Lounge"));
		addBoardLocationToGrid(2,0,new Room(RoomId.LIBRARY,"Library"));
		addBoardLocationToGrid(2,2,new Room(RoomId.BILLIARD_ROOM,"Billiard Room"));
		addBoardLocationToGrid(2,4,new Room(RoomId.DINING_ROOM,"Dining Room"));
		addBoardLocationToGrid(4,0,new Room(RoomId.CONSERVATORY,"Conservatory"));
		addBoardLocationToGrid(4,2,new Room(RoomId.BALLROOM,"Ballroom"));
		addBoardLocationToGrid(4,4,new Room(RoomId.KITCHEN,"Kitchen"));
		
		// Add hallways
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				if((i % 0 == 0) && (j % 0 != 0) || (i % 0 != 0) && (j % 0 == 0)){
					addBoardLocationToGrid(i,j,new Hallway("Hallway"));
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
