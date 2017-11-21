package clue.common;

import java.util.ArrayList;
import java.util.List;

public class Player {
	protected String playerName;
	protected List<Card> cards;
	protected Token token;
	
	// Constructor
	public Player(String playerName) {
		this.playerName = playerName;
		cards = new ArrayList<Card>();
		this.token = null;
	}

	// Get methods
	public String getPlayerName() {
		return playerName;
	}
	
	// Token modifier methods
	public Token getToken() {
		return token;
	}
	public boolean hasToken() {
		return (token != null);
	}
	public void assignToken(Token token) {
		// Remove token (if any)
		removeToken();
		// Set the new one
		token.setAvailable(false);
		this.token = token;
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
