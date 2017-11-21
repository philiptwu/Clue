package clue.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
		// Create a new game board but don't place tokens or weapons yet
		gameBoard = new GameBoard();
		
		// Create a complete set of cards but don't deal them yet
		roomCards = RoomCard.getCards();
		tokenCards = TokenCard.getCards();
		weaponCards = WeaponCard.getCards();
		
		// Initialize empty list of players
		players = new ArrayList<Player>();
	}
	
	// Gets a player by player name
	public Player getPlayer(String playerName) {
		for(Player p : players) {
			if(p.getPlayerName().equals(playerName)) {
				return p;
			}
		}
		return null;
	}
	
	// Get the gameboard
	public GameBoard getGameBoard() {
		return gameBoard;
	}
	
	// Create and add a new player only if another player with the same name does not already exist in the game
	public boolean addPlayer(String playerName) {
		// See if a player of that name already exists in game 
		if(getPlayer(playerName) != null) {
			System.err.println("Cannot create a player with name " + playerName + " since one already exists in the game");
			return false;			
		}else {
			// Add if that player does not exist yet
			Player newPlayer = new Player(playerName);
			players.add(newPlayer);
			return true;			
		}
	}
	
	// Remove a player by name
	public boolean removePlayer(String playerName) {
		// Look for player of that name
		int removeIdx = -1;
		for(int i=0; i<players.size(); i++) {
			if(players.get(i).getPlayerName().equals(playerName)) {
				removeIdx = i;
				break;
			}
		}
		// Remove if found and return whether or not that name was removed successfully
		if(removeIdx >= 0) {
			players.remove(removeIdx);
			return true;
		}else {
			System.err.println("Cannot remove player with name " + playerName + " since no such player exists in the game");
			return false;
		}
	}
	
	// Get the set of available tokens to choose from
	public List<Token> getAvailableTokens(){
		List<Token> availableTokens = new ArrayList<Token>();
		for(Token t : gameBoard.getTokens()) {
			if(t.getAvailable()) {
				availableTokens.add(t);
			}
		}
		return availableTokens;
	}
	
	// Takes existing room, token, and weapon cards
	// Removes one from each to create a game solution and then
	// deals the rest out randomly amongst players
	private void createSolutionAndDealCards() {		
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
		
		// Shuffle the cards
		Collections.shuffle(dealableCards,random);
		
		// Deal it to the available players
		int playerCount = players.size();
		int playerIdx = 0;
		if(playerCount == 0) {
			System.err.println("No players in the game, cannot deal cards");
		}else {
			System.out.println("Dealing " + dealableCards.size() + " cards to " + players.size() + " players");
			for(Card c : dealableCards) {
				players.get(playerIdx).acceptCard(c);
				playerIdx = (playerIdx + 1 ) % playerCount;
			}					
		}
	}	
	
	public static void main(String[] args) {
		Game g = new Game();
		g.addPlayer("Philip");
		g.addPlayer("Philip2");
		
		Player philip = g.getPlayer("Philip");
		
		List<Token> gameTokens; 
		gameTokens = g.getAvailableTokens();
		System.out.println("\nGame Tokens:");
		for(Token t : gameTokens) {
			System.out.println(t.getDisplayName());
		}

		for(Token t : gameTokens) {
			System.out.println("\nAssigning token " + t.getDisplayName() + "...");
			philip.assignToken(t);
			philip.assignToken(t);
			List<Token> availableTokens = g.getAvailableTokens();
			System.out.println("Available Tokens:");
			for(Token t2 : availableTokens) {
				System.out.println(t2.getDisplayName());
			}
		}
		
		System.out.println("");
		g.createSolutionAndDealCards();
	}
}
