package clue.result;

public class PlayerActionResult extends GameResult {
	public enum ActionResultType{
		ACTION_ACCEPTED(0,"Action Accepted"),
		ACTION_REJECTED(1,"Action Rejected");
		
		private final int id;
		private final String displayName;
		ActionResultType(int id, String displayName){
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
	protected ActionResultType resultType;
	protected String message;
	
	// Constructor
	public PlayerActionResult(GameResultCommunicationType gameResultCommunicationType, String playerId,
			ActionResultType playerStatusType, String message) {
		super(GameResultType.PLAYER_ACTION_RESULT,gameResultCommunicationType,playerId);
		this.resultType = playerStatusType;
		this.message = message;
	}
	
	// Get methods
	public ActionResultType getPlayerActionResultType() {
		return resultType;
	}
	
	// Print
	public String toString() {
		return resultType.toString() + ": " + message;
	}
}
