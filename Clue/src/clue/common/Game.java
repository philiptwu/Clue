package clue.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import clue.action.PlayerAction;
import clue.action.PlayerAction.PlayerActionType;
import clue.action.PlayerActionJoinGame;
import clue.common.GameBoard.MoveDirection;
import clue.status.PlayerStatus;
import clue.status.PlayerStatus.PlayerStatusType;

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
	protected int randomSeed;
	
	// Constructor
	public Game(int randomSeed) {
		// Save the random seed
		this.randomSeed = randomSeed;
		
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
	public PlayerStatus checkIfGameCanStart() {
		// Game status must be in INITIALIZING
		if(gameStatus != GameStatus.INITIALIZING) {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Game cannot start since it was already started");
		}
		
		// Need at least 3 players
		if(players.size() < MIN_PLAYERS) {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot start the game with less than 3 players, currently have " + players.size());
		}
		
		// All players must have tokens
		for(Player p : players) {
			if(!p.hasToken()) {
				return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
						"Cannot start the game because player " + p.getPlayerName() + " has not claimed a token yet");
			}
		}
			
		// If we got this far then we can start
		return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"All pre-conditions to start the game have been satisfied");
	}
	
	// Transitions game status from INITIALIZING to PLAYING only if all pre-conditions are met
	public PlayerStatus startGame() {
		// Check if game can start
		PlayerStatus ps = checkIfGameCanStart();

		// Return error if we got one while checking if game can start
		if(ps.getplayerStatusType() == PlayerStatusType.ACTION_REJECTED) {
			return ps;
		}
				
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
		
		// Report success
		return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"The game has started");
	}

	// Get the set of valid player actions
	public List<PlayerActionType> getValidPlayerActions(String playerId){
		// Create the empty list of actions
		List<PlayerActionType> validActions = new ArrayList<PlayerActionType>();
				
		// Specify what actions are valid and when
		if(gameStatus == GameStatus.INITIALIZING) {
			TBD
		}else if(gameStatus == gameStatus.PLAYING) {
			TBD
		}else if(gameStatus == gameStatus.FINISHED) {
			// Do nothing
		}
		
		// Return the list
		return validActions;
	}
	
	// Get the set of valid move directions
	public Set<MoveDirection> getValidMoveDirections() {
		Token t = players.get(turnPlayerIdx).getToken();
		return gameBoard.getMoveDirections(t);
	}
	
	// Move the token to one of the valid move directions
	public PlayerStatus move(String playerId, MoveDirection m) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot move if game is not playing
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot move when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player currPlayer = players.get(turnPlayerIdx);
		if(!currPlayer.getPlayerName().equals(playerId)) {
			// Cannot move when it is not your turn
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot move when it is not your turn");
		}
		
		// Get the set of valid move directions
		Set<MoveDirection> validMoveDirections = getValidMoveDirections();
		
		// Make sure proposed direction is in that list
		if(validMoveDirections.contains(m)) {
			// Move is valid
			Token t = players.get(turnPlayerIdx).getToken();
			gameBoard.moveToken(t,m);
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"Player " + playerId + " successfully moved in direction " + m.toString());
		}else {
			// Move is not valid
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
				"Player " + playerId + " cannot move in direction " + m.toString());
		}
	}
	
	// Make a suggestion
	public void suggest(GameSolution suggestion) {
		// TODO
	}
	
	// Make an accusation, wins game if correct, ends turn (and loses) if incorrect
	public PlayerStatus accuse(String playerId, String roomId, String tokenId, String weaponId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot accuse if game is not playing
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot make an accusation when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player currPlayer = players.get(turnPlayerIdx);
		if(!currPlayer.getPlayerName().equals(playerId)) {
			// Cannot accuse when it is not your turn
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot make an accusation when it is not your turn");
		}
		
		// Get the room
		List<Room> rooms = gameBoard.getRooms();
		Room accusedRoom = null;
		for(Room r : rooms) {
			if(r.getDisplayName().equals(roomId)) {
				accusedRoom = r;
			}
		}
		if(accusedRoom == null) {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot make an accusation with room " + roomId + " because it does not exist in the game");
		}
		
		// Get the token
		List<Token> tokens = gameBoard.getTokens();
		Token accusedToken = null;
		for(Token t : tokens) {
			if(t.getDisplayName().equals(tokenId)) {
				accusedToken = t;
			}
		}
		if(accusedToken == null) {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot make an accusation with token " + tokenId + " because it does not exist in the game");
		}
		
		// Get the weapon
		List<Weapon> weapons = gameBoard.getWeapons();
		Weapon accusedWeapon = null;
		for(Weapon w : weapons) {
			if(w.getDisplayName().equals(weaponId)) {
				accusedWeapon = w;
			}
		}
		if(accusedWeapon == null) {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot make an accusation with weapon " + weaponId + " because it does not exist in the game");
		}
		
		// Create the game solution
		GameSolution accusation = new GameSolution(accusedRoom.getRoomId(), accusedToken.getTokenId(), accusedWeapon.getWeaponId());
		
		// Check the game solution
		if(gameSolution.equals(accusation)) {
			// Accusation is correct, player wins
			gameStatus = GameStatus.FINISHED;
			winnerIdx = turnPlayerIdx;
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"Accusation of " + accusation + " is correct, player wins!");
			
		}else {
			// Accusation is incorrect, set the player as inactive and end their turn automatically
			Player loser = players.get(turnPlayerIdx);
			loser.setInactive();			
			gameBoard.moveTokenToClosestRoom(loser.getToken());
			endTurn();

			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"Accusation of " + accusation + " is incorrect, player loses!");
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
	
	// Gets a list of active players
	private List<Player> getActivePlayers(){
		List<Player> activePlayers = new ArrayList<Player>();
		for(Player p : players) {
			if(p.getActive()) {
				activePlayers.add(p);
			}
		}
		return activePlayers;
	}
	
	// Create and add a new player only if another player with the same name does not already exist in the game
	public PlayerStatus addPlayer(String playerName) {
		// Determine if we can take the action
		if(gameStatus != GameStatus.INITIALIZING) {
			// Cannot add players after game has started
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot add any players after game has started");
		}else if(getPlayer(playerName) != null) {
			// Cannot duplicate players
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED, 
					"Cannot create a player with name " + playerName + " since one already exists in the game"); 			
		}else if(players.size() >= MAX_PLAYERS){
			// Too many players already
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Already reached max number of players " + MAX_PLAYERS + ", cannot add any more");
		}else {
			// Add if that player does not exist yet
			Player newPlayer = new Player(playerName);
			players.add(newPlayer);
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
					"Player " + playerName + " has joined the game");
		}
	}
	
	// Remove a player by name
	public PlayerStatus removePlayer(String playerName) {
		// Determine if we can take the action
		if(gameStatus != GameStatus.INITIALIZING) {
			// Cannot add players after game has started
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot remove any players after game has started");
		}
		
		// Find the player
		int removeIdx = -1;
		for(int i=0; i<players.size(); i++) {
			if(players.get(i).getPlayerName().equals(playerName)) {
				removeIdx = i;
				break;
			}
		}
					
		// Remove if found and return whether or not that name was removed successfully
		if(removeIdx >= 0) {
			// Return token if necessary
			if(players.get(removeIdx).hasToken()) {
				players.get(removeIdx).removeToken();
			}
						
			// Remove player from game
			players.remove(removeIdx);
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
				"Player " + playerName + " has left the game");
		}else {
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot remove player " + playerName + " since no such player exists in the game");
		}	
	}
	
	// Assign token
	public PlayerStatus assignToken(String playerName, String tokenName) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.INITIALIZING ) {
			// Cannot assign token after game has started
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot assign tokens after game has started");
		}
		
		// Get the player
		Player p = getPlayer(playerName);
		if(p == null) {
			// No player found
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot assign token to player " + playerName + " since no such player exists in the game");
		}
		
		// See if player already has a token
		if(p.hasToken()) {
			// Cannot get another token
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Player " + playerName + " already has a token, must discard the token first before selecting a new one");
		}
		
		// Get the token
		List<Token> tokens = gameBoard.getTokens();
		for(Token t : tokens) {
			if(t.getDisplayName().equals(tokenName)) {
				// Found the token
				if(t.getAvailable()) {
					// Token is available, grab it
					p.assignToken(t);
					return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
							"Player " + playerName + " has been assigned the token " + tokenName);
				}else {
					// Token is not available
					return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
							"The token " + tokenName + " is already taken by another player");
				}
			}
		}
		
		// If we got here then no token was found
		return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
				"The token " + tokenName + " does not exist");
	}
	
	// Discard token
	public PlayerStatus discardToken(String playerName) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.INITIALIZING ) {
			// Cannot assign token after game has started
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot discard tokens after game has started");
		}
		
		// Get the player
		Player p = getPlayer(playerName);
		if(p == null) {
			// No player found
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Cannot discard token of player " + playerName + " since no such player exists in the game");
		}
		
		// See if player already has a token
		if(p.hasToken()) {
			// Remove the token
			Token t = p.getToken();
			p.removeToken();
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_ACCEPTED,
					"Player " + playerName + " has discarded the token " + t.getDisplayName());			
		}else {
			// Cannot discard a token that doesn't exist
			return new PlayerStatus(PlayerStatus.PlayerStatusType.ACTION_REJECTED,
					"Player " + playerName + " does not have a token to discard");
		}
	}
	
	// Get the set of available tokens to choose from
	private List<Token> getAvailableTokens(){
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
		Random random = new Random(randomSeed);
		
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
	
	/*public static void main(String[] args) {
		Game g = new Game(0);
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
		
	}*/
}
