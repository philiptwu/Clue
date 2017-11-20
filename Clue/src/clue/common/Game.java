package clue.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import clue.common.Token.TokenId;

public class Game {
	// Member variables
	protected GameBoard gameBoard;
	protected List<RoomCard> roomCards;
	protected List<TokenCard> tokenCards;
	protected List<WeaponCard> weaponCards;
	protected List<Player> players;
	protected GameSolution gameSolution;
	
	// Constructor
	public Game() {		
		// Create a new game board but don't place tokens or weapons
		gameBoard = new GameBoard();
		
		// Create a complete set of cards but don't deal them yet
		roomCards = RoomCard.getCards();
		tokenCards = TokenCard.getCards();
		weaponCards = WeaponCard.getCards();
		
		// Initialize empty list of players
		players = new ArrayList<Player>();
	}
	
	// Takes existing room, token, and weapon cards
	// Removes one from each to create a game solution and then
	// deals the rest out randomly amongst players
	public void createSolutionAndDealCards() {		
		// Create random number generator
		Random random = new Random();
		
		// Choose a solution randomly
		int roomSolutionIdx = random.nextInt(roomCards.size());
		int tokenSolutionIdx = random.nextInt(tokenCards.size());
		int weaponSolutionIdx = random.nextInt(weaponCards.size());
		gameSolution = new GameSolution(roomCards.get(roomSolutionIdx).getRoomId(),
				tokenCards.get(tokenSolutionIdx).getTokenId(),
				weaponCards.get(weaponSolutionIdx).getWeaponId());
		
		// Create a list of all the cards that weren't in the solution
		List<Card> dealableCards = new ArrayList<Card>();
		for(int i=0; i<roomCards.size(); i++) {
			if(i != roomSolutionIdx) {
				dealableCards.add(roomCards.get(i));
			}
		}
		for(int i=0; i<tokenCards.size(); i++) {
			if(i != tokenSolutionIdx) {
				dealableCards.add(tokenCards.get(i));
			}
		}
		for(int i=0; i<weaponCards.size(); i++) {
			if(i != weaponSolutionIdx) {
				dealableCards.add(weaponCards.get(i));
			}
		}
		
		// Deal it to the available players
		TODO
		
	}	
}
