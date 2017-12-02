package clue.action;

public abstract class PlayerAction {
	public enum PlayerActionType{
		JOIN_GAME(0,"Join Game"),
		LEAVE_GAME(1,"Leave Game"),
		CHOOSE_TOKEN(2,"Choose Token"),
		DISCARD_TOKEN(3,"Discard Token"),
		VOTE_START_GAME(4,"Start Game"),
		MOVE(5,"Move"),
		MAKE_SUGGESTION(6,"Make Suggestion"),
		SHOW_CARD(7,"Show Card"),
		MAKE_ACCUSATION(8,"Make Accusation"),
		END_TURN(9,"End Turn");
		
		private final int id;
		private final String displayName;
		PlayerActionType(int id, String displayName){
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
	protected PlayerActionType playerActionType;
	protected String playerId;
	
	// Constructor
	public PlayerAction(String playerId, PlayerActionType playerActionType) {
		this.playerId = playerId;
		this.playerActionType = playerActionType;
	}
	
	// Get methods
	public PlayerActionType getPlayerActionType() {
		return playerActionType;
	}
	public String getPlayerId() {
		return playerId;
	}
	
	// Reverse lookup PlayerActionType by value
	public static PlayerActionType getPlayerActionTypeByValue(int id) {
		for(PlayerActionType pat : PlayerActionType.values()) {
			if(id == pat.getValue()) {
				return pat;
			}
		}
		return null;
	}
}
