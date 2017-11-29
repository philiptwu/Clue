package clue.action;

public class PlayerActionDiscardToken extends PlayerAction {
	// Member variables
	protected String tokenId;
	
	// Constructor
	public PlayerActionDiscardToken(String playerId, String tokenId) {
		super(playerId, PlayerActionType.DISCARD_TOKEN);
		this.tokenId = tokenId;
	}
	
	// Get methods
	public String getTokenId() {
		return tokenId;
	}
	
	// Output
	public String toString() {
		return "Player " + playerId + " discards token " + tokenId;
	}
}
