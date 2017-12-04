package clue.common;

public abstract class Card {
	// Enumeration
	public enum CardType{
		ROOM(0,"Room"),
		TOKEN(1,"Token"),
		WEAPON(2,"Weapon");
		
		private final int id;
		private final String displayName;
		CardType(int id, String displayName){
			this.id = id;
			this.displayName = displayName;
		}
		public int getValue() {
			return id;
		}
		public String toString() {
			return displayName;
		}
	}
	
	// Member variables
	protected CardType cardType;
	
	// Constructor
	public Card(CardType cardType) {
		this.cardType = cardType;
	}
		
	// Reverse lookup card type by value
	public static CardType getCardTypeByValue(int id) {
		for(CardType ct : CardType.values()) {
			if(id == ct.getValue()) {
				return ct;
			}
		}
		return null;
	}
	
	// Get methods
	public CardType getCardType() {
		return cardType;
	}
}
