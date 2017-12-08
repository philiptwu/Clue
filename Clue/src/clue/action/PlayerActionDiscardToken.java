package clue.action;

public class PlayerActionDiscardToken extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionDiscardToken(String playerId) {
		super(playerId, PlayerActionType.DISCARD_TOKEN);
	}
	
	// Get methods
	
	// Output
	public String toString() {
		return "Player " + playerId + " discards token";
	}
}
