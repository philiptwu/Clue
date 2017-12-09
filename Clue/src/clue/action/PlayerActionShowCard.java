package clue.action;

public class PlayerActionShowCard extends PlayerAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1891949660941950384L;
	
	// Member variables
	protected String cardType;
	protected String cardId;
	
	// Constructor
	public PlayerActionShowCard(String playerId, String cardType, String cardId) {
		super(playerId, PlayerActionType.SHOW_CARD);
		this.cardType = cardType;
		this.cardId = cardId;
	}
	
	// Get methods
	public String getCardType() {
		return cardType;
	}
	public String getCardId() {
		return cardId;
	}
	
	// Output
	public String toString() {
		return "Player " + playerId + " shows the card: " + cardId + " (" + cardType + ")";
	}
}
