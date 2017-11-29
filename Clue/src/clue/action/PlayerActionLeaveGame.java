package clue.action;

public class PlayerActionLeaveGame extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionLeaveGame(String playerId) {
		super(playerId, PlayerActionType.LEAVE_GAME);		
	}
	
	// Get methods
	
	// Output
	public String toString() {
		return "Player " + playerId + " requesting to leave game";
	}
}
