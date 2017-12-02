package clue.action;

public class PlayerActionEndTurn extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionEndTurn(String playerId) {
		super(playerId, PlayerActionType.END_TURN);		
	}
	
	// Get methods
	
	// Output
	public String toString() {
		return "Player " + playerId + " requesting to end turn";
	}
}
