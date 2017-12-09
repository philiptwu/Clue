package clue.action;

public class PlayerActionMakeSuggestion extends PlayerAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7520564531721589491L;
	
	// Member variables
	protected String roomId;
	protected String tokenId;
	protected String weaponId;
	
	// Constructor
	public PlayerActionMakeSuggestion(String playerId, String roomId, String tokenId, String weaponId) {
		super(playerId, PlayerActionType.MAKE_SUGGESTION);
		this.roomId = roomId;
		this.tokenId = tokenId;
		this.weaponId = weaponId;
	}
	
	// Get methods
	public String getRoomId() {
		return roomId;
	}
	public String getTokenId() {
		return tokenId;
	}
	public String getWeaponId() {
		return weaponId;
	}
	
	// Output
	public String toString() {
		return "Player " + playerId + " makes the suggestion: " + tokenId + "with " + weaponId + " in the " + roomId;
	}
}
