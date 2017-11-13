package clue.common;

public class Hallway extends BoardLocation {
	
	// Constructor
	public Hallway(String displayName) {
		// Parent constructor, hallways can hold at most one person
		super(displayName,1);				
	}	
}
