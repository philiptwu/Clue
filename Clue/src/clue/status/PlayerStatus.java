package clue.status;

public class PlayerStatus {
	public enum PlayerStatusType{
		ACTION_ACCEPTED(0,"Action Accepted"),
		ACTION_REJECTED(1,"Action Rejected");
		
		private final int id;
		private final String displayName;
		PlayerStatusType(int id, String displayName){
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
	protected PlayerStatusType playerStatusType;
	protected String message;
	
	// Constructor
	public PlayerStatus(PlayerStatusType playerStatusType, String message) {
		this.playerStatusType = playerStatusType;
		this.message = message;
	}
	
	// Get methods
	public PlayerStatusType getplayerStatusType() {
		return playerStatusType;
	}
	
	// Print
	public String toString() {
		return playerStatusType.toString() + ": " + message;
	}
}
