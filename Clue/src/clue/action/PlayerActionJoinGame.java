package clue.action;

public class PlayerActionJoinGame extends PlayerAction {
	// Member variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6839453386142224836L;

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
