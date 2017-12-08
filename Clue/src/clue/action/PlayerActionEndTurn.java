package clue.action;

public class PlayerActionEndTurn extends PlayerAction {
	// Member variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1227364760234624168L;

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
