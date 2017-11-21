package clue.common;

public class Room extends BoardLocation {
	// Enumeration
	public enum RoomId{
		STUDY(0,"Study"),
		HALL(1,"Hall"),
		LOUNGE(2,"Lounge"),
		LIBRARY(3,"Library"),
		BILLIARD_ROOM(4,"Billiard Room"),
		DINING_ROOM(5,"Dining Room"),
		CONSERVATORY(6,"Conservatory"),
		BALLROOM(7,"Ballroom"),
		KITCHEN(8,"Kitchen");
		
		private final int id;
		private final String defaultName;
		RoomId(int id, String defaultName){
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
	protected RoomId roomId;
	
	// Constructor
	public Room(RoomId roomId) {
		// Parent constructor, effectively no limit to room capacity
		super(roomId.getDefaultName(),Integer.MAX_VALUE);
				
		// Set member variables
		this.roomId = roomId;
	}
	
	// Get methods
	public RoomId getRoomId() {
		return roomId;
	}
}
