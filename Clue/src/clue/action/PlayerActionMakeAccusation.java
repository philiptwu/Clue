package clue.action;

public class PlayerActionMakeAccusation extends PlayerAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7131959867427521090L;
	// Member variables
	protected String roomId;
	protected String tokenId;
	protected String weaponId;
	
	// Constructor
	public PlayerActionMakeAccusation(String playerId, String roomId, String tokenId, String weaponId) {
		super(playerId, PlayerActionType.MAKE_ACCUSATION);
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
		return "Player " + playerId + " makes the accuses " + tokenId + "with " + weaponId + " in the " + roomId;
	}
}
