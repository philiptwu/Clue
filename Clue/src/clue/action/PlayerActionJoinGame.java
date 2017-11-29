package clue.action;

public class PlayerActionJoinGame extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionJoinGame(String playerId) {
		super(playerId, PlayerActionType.JOIN_GAME);		
	}
	
	// Get methods

	// Output
	public String toString() {
		return "Player " + playerId + " requesting to join game";
	}
}
