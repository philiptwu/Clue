package clue.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import clue.common.GameBoard.MoveDirection;

public class Game {
	// Enumeration
	public enum GameStatus{
		INITIALIZING(0,"Initializing"),
		PLAYING(1,"Playing"),
		FINISHED(2,"Finished");
		
		private final int id;
		private final String displayName;
		GameStatus(int id, String displayName){
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
	
	// Constant
	public static final int MIN_PLAYERS = 3;
	public static final int MAX_PLAYERS = 6;
	
	// Member variables
	protected GameBoard gameBoard;
	protected List<RoomCard> roomCards;
	protected List<TokenCard> tokenCards;
	protected List<WeaponCard> weaponCards;
	protected List<Player> players;
	protected GameSolution gameSolution;
	protected GameStatus gameStatus;
	protected int turnPlayerIdx;
	protected boolean turnPlayerMoved;
	protected boolean turnPlayerSuggested;
	protected int winnerIdx;
	
	// Constructor
	public Game() {						
		// Game is initializing
		gameStatus = GameStatus.INITIALIZING;
		
		// Create a new game board but don't place tokens or weapons yet
		gameBoard = new GameBoard();
		
		// Create a complete set of cards but don't deal them yet
		roomCards = RoomCard.getCards();
		tokenCards = TokenCard.getCards();
		weaponCards = WeaponCard.getCards();
		
		// Initialize empty list of players
		players = new ArrayList<Player>();
		
		// No winners yet
		winnerIdx = -1;
		
		// No one has the turn
		turnPlayerIdx = -1;
	}
	
	// Checks if the game satisfies all pre-conditions to start can start
	public boolean checkIfGameCanStart() {
		// Game status must be in INITIALIZING
		if(gameStatus != GameStatus.INITIALIZING) {
			System.err.println("Game status is \"" + gameStatus + "\" but must be \"" + 
				GameStatus.INITIALIZING + "\" to start game");
			return false;
		}
		
		// Need at least 3 players
		if(players.size() < MIN_PLAYERS) {
			System.err.println("Cannot start the game with less than 3 players, currently have " + players.size());
			return false;
		}
		
		// All players must have tokens
		for(Player p : players) {
			if(!p.hasToken()) {
				System.err.println("Cannot start the game because player " + p.getPlayerName() + " has not claimed a token yet");
				return false;
			}
		}
			
		// If we got this far then we can start
		return true;
	}
	
	// Transitions game status from INITIALIZING to PLAYING only if all pre-conditions are met
	public boolean startGame() {
		// Check if game can start
		if(checkIfGameCanStart()) {
			// Sort player order based on their selected token
			Collections.sort(players, new Comparator<Player>() {
				@Override
				public int compare(Player p1, Player p2) {
					Integer p1Value = new Integer(p1.getToken().getTokenId().getValue());
					Integer p2Value = new Integer(p2.getToken().getTokenId().getValue());
					return p1Value.compareTo(p2Value);
				}
			});
			
			// Make it the first player's turn
			turnPlayerIdx = 0;
			
			// Create solution and deal cards
			createSolutionAndDealCards();
			
			// Transition game state
			gameStatus = GameStatus.PLAYING;
			return true;
		}else {
			// Error message
			System.err.println("Unable to start game, not all pre-conditions have been met");
			return false;
		}
	}

	// Get the set of valid move directions
	public Set<MoveDirection> getValidMoveDirections() {
		Token t = players.get(turnPlayerIdx).getToken();
		return gameBoard.getMoveDirections(t);
	}
	
	// Move the token to one of the valid move directions
	public boolean move(MoveDirection m) {
		// Get the set of valid move directions
		Set<MoveDirection> validMoveDirections = getValidMoveDirections();
		
		// Make sure proposed direction is in that list
		if(validMoveDirections.contains(m)) {
			// Move is valid
			Token t = players.get(turnPlayerIdx).getToken();
			gameBoard.moveToken(t,m);
			return true;
		}else {
			// Move is not valid
			System.err.println("Selected move direction " + m + " is not valid");
			return false;
		}
	}
	
	// Make a suggestion
	public void suggest(GameSolution suggestion) {
		// TODO
	}
	
	// Make an accusation, wins game if correct, ends turn (and loses) if incorrect
	public void accuse(GameSolution accusation) {
		if(gameSolution.equals(accusation)) {
			// Accusation is correct, player wins
			System.out.println("Accusation of " + accusation + " is correct, player wins!");
			gameStatus = GameStatus.FINISHED;
			winnerIdx = turnPlayerIdx;
		}else {
			// Accusation is incorrect, set the player as inactive and end their turn automatically
			System.out.println("Accusation of " + accusation + " is incorrect, player loses!");
			Player loser = players.get(turnPlayerIdx);
			loser.setInactive();
			gameBoard.moveTokenToClosestRoom(loser.getToken());
			endTurn();
		}
	}
	
	// Ends the current player's turn and readies the next player
	public void endTurn() {
		// Switch to the next player that is active
		while(true) {
			turnPlayerIdx = (turnPlayerIdx + 1 ) % players.size();
			if(players.get(turnPlayerIdx).getActive()) {
				break;
			}
		}	
		
		// Reset bookkeeping flags for this turn
		turnPlayerMoved = false;
		turnPlayerSuggested = false;
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
	
	// Gets the player who has the current turn
	public Player getTurnPlayer() {
		if(turnPlayerIdx < 0) {
			return null;
		}else {
			return players.get(turnPlayerIdx);
		}
	}
	
	// Create and add a new player only if another player with the same name does not already exist in the game
	public Player addPlayer(String playerName) {
		// See if a player of that name already exists in game 
		if(getPlayer(playerName) != null) {
			System.err.println("Cannot create a player with name " + playerName + " since one already exists in the game");
			return null;			
		}else if(players.size() >= MAX_PLAYERS){
			System.err.println("Already reached max number of players " + MAX_PLAYERS + ", cannot add any more");
			return null;
		}else {
			// Add if that player does not exist yet
			Player newPlayer = new Player(playerName);
			players.add(newPlayer);
			return newPlayer;			
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
			for(Card c : dealableCards) {
				players.get(playerIdx).acceptCard(c);
				playerIdx = (playerIdx + 1 ) % playerCount;
			}					
		}
	}
	
	public static void main(String[] args) {
		Game g = new Game();
		Player philip = g.addPlayer("Philip");
		Player angel = g.addPlayer("Angel");
		Player george = g.addPlayer("George");
		
		List<Token> gameTokens; 
		gameTokens = g.getAvailableTokens();
		System.out.println("Available tokens: " );
		for(Token t : gameTokens) {
			System.out.println(t.getDisplayName() + " at " + t.getLocationX() + "," + t.getLocationY());
		}
		Token tk = gameTokens.get(0);
		System.out.println("Assigning " + tk.getDisplayName() + " to " + philip.getPlayerName());
		philip.assignToken(tk);

		tk = gameTokens.get(1);
		System.out.println("Assigning " + tk.getDisplayName() + " to " + angel.getPlayerName());
		angel.assignToken(tk);
		
		tk = gameTokens.get(2);
		System.out.println("Assigning " + tk.getDisplayName() + " to " + philip.getPlayerName());
		philip.assignToken(tk);
		
		tk = gameTokens.get(0);
		System.out.println("Assigning " + tk.getDisplayName() + " to " + george.getPlayerName());		
		george.assignToken(tk);
		
		System.out.println("Game can start : " + g.checkIfGameCanStart());
		
		g.startGame();
		
		Player turnPlayer = g.getTurnPlayer();
		System.out.println("\n" + turnPlayer.getPlayerName() + " location: (" + turnPlayer.getToken().getLocationX() + "," + turnPlayer.getToken().getLocationY() + ")");
				
		System.out.println("\nValid move directions:");
		Set<MoveDirection> validMoveDirections = g.getValidMoveDirections();
		MoveDirection turnMoveDirection = null;
		for(MoveDirection m : validMoveDirections) {
			turnMoveDirection = m;
			System.out.println(m);
		}

		System.out.println("\nMoving " + turnMoveDirection);
		g.move(turnMoveDirection);
		
		System.out.println("\n" + turnPlayer.getPlayerName() + " location: (" + turnPlayer.getToken().getLocationX() + "," + turnPlayer.getToken().getLocationY() + ")");
		
		System.out.println("\nValid move directions:");
		validMoveDirections = g.getValidMoveDirections();
		for(MoveDirection m : validMoveDirections) {
			System.out.println(m);
		}
		
	}
}
