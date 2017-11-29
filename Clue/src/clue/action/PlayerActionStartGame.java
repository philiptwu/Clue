package clue.action;

public class PlayerActionStartGame extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionStartGame(String playerId) {
		super(playerId, PlayerActionType.START_GAME);		
	}
	
	// Get methods
	
	// Output
	public String toString() {
		return "Player " + playerId + " requesting to start game";
	}
}
