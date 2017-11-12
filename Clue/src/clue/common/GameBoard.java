package clue.common;

import java.util.HashSet;
import java.util.Set;

public class GameBoard {
	// Member variables
	Set<Token> tokens;
	Set<Weapon> weapons;
	Set<Room> rooms;
	Set<Hallway> hallways;
	BoardLocation[][] grid;
	
	// Constructor to create a blank initialized game board
	public GameBoard() {
		// Initialize data structures
		tokens = new HashSet<Token>();
		weapons = new HashSet<Weapon>();
		rooms = new HashSet<Room>();
		hallways = new HashSet<Hallway>();
		grid = new BoardLocation[6][6];
 		
		// Initialize the game grid
		initialize();
	}	
	
	// Method to initialize a game grid
	public void initialize() {
		//TODO: Continue here!
	}
}
