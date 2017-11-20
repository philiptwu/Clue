package clue.common;

public abstract class Card {
	// Enumeration
	public enum CardType{
		ROOM(0),
		TOKEN(1),
		WEAPON(2);
		
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
	
	// Constructor
	public Card(CardType cardType) {
		this.cardType = cardType;
	}
		
	// Get methods
	public CardType getCardType() {
		return cardType;
	}
}
