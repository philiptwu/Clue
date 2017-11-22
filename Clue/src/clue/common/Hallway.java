package clue.common;

public class Hallway extends BoardLocation {
	
	// Constructor
	public Hallway() {
		// Parent constructor, hallways can hold at most one person
		super(LocationType.HALLWAY,"Hallway",1);				
	}	
}
