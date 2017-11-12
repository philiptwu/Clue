package clue.common;

public class Card {
	// Enumeration
	public enum CardType{
		ACTOR(0),
		WEAPON(1),
		ROOM(2);
		
		private final int id;
		CardType(int id){
			this.id = id;
		}
		public int getValue() {
			return id;
		}
	}
	
	// Member variables
	protected CardType cardType;
	protected int cardId;
	
	// Constructor
	public Card(CardType cardType, int cardId) {
		this.cardType = cardType;
		this.cardId = cardId;
	}
	
	// Get methods
	public CardType getCardType() {
		return cardType;
	}
	public int getCardId() {
		return cardId;
	}
}
