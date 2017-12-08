package clue.action;

public class PlayerActionLeaveGame extends PlayerAction {
	// Member variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8549576033958884508L;

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
