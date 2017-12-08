package clue.action;

public class PlayerActionDiscardToken extends PlayerAction {
	// Member variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4642933158737583116L;

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
