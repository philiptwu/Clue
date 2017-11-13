package clue.common;

public class Room extends BoardLocation {
	// Enumeration
	public enum RoomId{
		STUDY(0),
		HALL(1),
		LOUNGE(2),
		LIBRARY(3),
		BILLIARD_ROOM(4),
		DINING_ROOM(5),
		CONSERVATORY(6),
		BALLROOM(7),
		KITCHEN(8);
		
		private final int id;
		RoomId(int id){
			this.id = id;
		}
		public int getValue() {
			return id;
		}
	}
	
	// Member variables
	protected RoomId roomId;
	
	// Constructor
	public Room(RoomId roomId, String displayName) {
		// Parent constructor, effectively no limit to room capacity
		super(displayName,Integer.MAX_VALUE);
				
		// Set member variables
		this.roomId = roomId;
	}
	
	// Get methods
	public RoomId getRoomId() {
		return roomId;
	}
}
