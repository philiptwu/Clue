package clue.common;

import java.util.ArrayList;
import java.util.List;

import clue.common.Token.TokenId;

public class TokenCard extends Card {
	// Member variables
	TokenId tokenId;
	
	// Constructor
	public TokenCard(TokenId tokenId) {
		super(CardType.TOKEN);		
		this.tokenId = tokenId;
	}
	
	public TokenId getTokenId() {
		return tokenId;
	}
	
	public String toString() {
		return cardType.toString() + " " + tokenId.getDefaultName();
	}
	
	public static List<TokenCard> getCards(){
		List<TokenCard> tokenCards = new ArrayList<TokenCard>();
		for(TokenId tokenId : TokenId.values()) {
			tokenCards.add(new TokenCard(tokenId));
		}
		return tokenCards;
	}
}
