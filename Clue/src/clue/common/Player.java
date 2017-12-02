package clue.common;

import java.util.ArrayList;
import java.util.List;

public class Player {
	protected String playerName;
	protected List<Card> cards;
	protected Token token;
	protected boolean active;
	protected boolean voted;
	
	// Constructor
	public Player(String playerName) {
		this.playerName = playerName;
		cards = new ArrayList<Card>();
		this.token = null;
		this.active = true;
		this.voted = false;
	}

	// Get methods
	public String getPlayerName() {
		return playerName;
	}
	public boolean getActive() {
		return active;
	}
	public boolean getVoted() {
		return voted;
	}
	public List<Card> getCards(){
		return new ArrayList<Card>(cards);
	}
	
	// Set methods
	public void setInactive() {
		this.active = false;
	}
	public void setVoted() {
		this.voted = true;
	}
	
	// Token modifier methods
	public Token getToken() {
		return token;
	}
	public boolean hasToken() {
		return (token != null);
	}
	public boolean assignToken(Token token) {
		if(token.getAvailable()) {
			// Assign this token, it is available
			// Remove token (if any)
			removeToken();
			// Set the new one
			token.setAvailable(false);
			this.token = token;
			return true;
		}else {
			// Cannot assign this token because it is not available
			System.out.println("Cannot assign token " + token.getDisplayName() + " because it is not available");
			return false;
		}
	}
	public void removeToken() {
		if(token != null) {
			token.setAvailable(true);
		}
		token = null;
	}
	
	// Card modifier methods
	public void acceptCard(Card newCard) {
		cards.add(newCard);
	}
	public void clearCards() {
		cards.clear();
	}
}
