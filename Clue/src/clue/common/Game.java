package clue.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import clue.action.PlayerAction.PlayerActionType;
import clue.common.BoardLocation.LocationType;
import clue.common.Card.CardType;
import clue.common.GameBoard.MoveDirection;
import clue.result.PlayerActionResult;
import clue.result.GameResult.GameResultCommunicationType;
import clue.result.PlayerActionResult.ActionResultType;

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
	public List<Player> players;
	protected GameSolution gameSolution;
	protected GameSolution currSuggestion;
	public GameStatus gameStatus;
	public int turnPlayerIdx;
	public int showCardPlayerIdx;
	public boolean turnPlayerMoved;
	public boolean turnPlayerSuggested;
	public boolean cardShowingFinished;
	public int winnerIdx;
	public String gameId;
	protected Random random;
	public List<Card> showableCards;
	
	// Constructor
	public Game(int randomSeed, String gameId) {
		// Create a random number generator
		random = new Random(randomSeed);
		
		// Save the game ID
		this.gameId = gameId;
		
		// Game is initializing
		gameStatus = GameStatus.INITIALIZING;
		
		// Create a new game board but don't place tokens or weapons yet
		gameBoard = new GameBoard(random);
		
		// Create a complete set of cards but don't deal them yet
		roomCards = RoomCard.getCards();
		tokenCards = TokenCard.getCards();
		weaponCards = WeaponCard.getCards();
		
		// Initialize
		showableCards = new ArrayList<Card>();
		
		// Initialize empty list of players
		players = new ArrayList<Player>();
		
		// No winners yet
		winnerIdx = -1;
		
		// No one has the turn
		turnPlayerIdx = -1;
	}
	
	// Checks if the game satisfies all pre-conditions to start can start
	private PlayerActionResult checkIfGameCanStart() {
		// Game status must be in INITIALIZING
		if(gameStatus != GameStatus.INITIALIZING) {
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Game cannot start since it was already started");
		}
		
		// Need at least 3 players
		if(players.size() < MIN_PLAYERS) {
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot start the game yet with less than 3 players, currently have " + players.size());
		}
		
		// All players must have tokens
		for(Player p : players) {
			if(!p.hasToken()) {
				return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
						PlayerActionResult.ActionResultType.ACTION_REJECTED,
						"Cannot start the game yet because player " + p.getPlayerName() + " has not claimed a token");
			}
		}
			
		// All players must be ready to start
		for(Player p : players) {
			if(!p.getVoted()) {
				return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
						PlayerActionResult.ActionResultType.ACTION_REJECTED,
						"Cannot start the game yet because player " + p.getPlayerName() + " has not voted to start the game");
			}
		}
		
		// If we got this far then we can start
		return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
				PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				"All pre-conditions to start the game have been satisfied");
	}
	
	// Transitions game status from INITIALIZING to PLAYING only if all pre-conditions are met
	public PlayerActionResult startGame() {
		// Check if game can start
		PlayerActionResult ps = checkIfGameCanStart();

		// Return error if we got one while checking if game can start
		if(ps.getPlayerActionResultType() == ActionResultType.ACTION_REJECTED) {
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
		return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
				PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				"The game has started");
	}
	
	// Player votes to start the game
	public PlayerActionResult voteStartGame(String playerId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.INITIALIZING ) {
			// Cannot vote to start the game if game has already started
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot vote to start a game that already started");
		}
		
		// Get the player
		Player p = getPlayer(playerId);
		if(p == null) {
			// No such player
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot vote to start the game because player " + playerId + " does not exist in the game");
		}else if(!p.hasToken()) {
			// Does not have token yet
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot vote to start the game because player " + playerId + " does not have a token yet");
		}else if(p.getVoted()) {
			// Already voted to start
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Player " + playerId + " has already voted to start the game");
		}else {
			// Conditions are good to start
			p.setVoted();
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
					"Player " + playerId + " has voted to start the game");
		}		
	}
	
	// Get the set of valid player actions
	public List<PlayerActionType> getValidPlayerActions(String playerId){
		// Create the empty list of actions
		List<PlayerActionType> validActions = new ArrayList<PlayerActionType>();
				
		// Specify what actions are valid and when
		if(gameStatus == GameStatus.INITIALIZING) {
				Player p = getPlayer(playerId);
				if(p == null) {
					// If player doesn't exist in the game, then the only thing it can do is join
					validActions.add(PlayerActionType.JOIN_GAME);
				}else if(!p.getVoted()){	
					// This player is already in the game
					if(p.hasToken()) {
						// Can discard existing token
						validActions.add(PlayerActionType.DISCARD_TOKEN);
						validActions.add(PlayerActionType.VOTE_START_GAME);
					}else {
						// Can grab a new token
						validActions.add(PlayerActionType.CHOOSE_TOKEN);
					}
					// Can always leave game
					validActions.add(PlayerActionType.LEAVE_GAME);
				}
		}else if(gameStatus == GameStatus.PLAYING) {
			if(turnPlayerSuggested && !cardShowingFinished) {
				// Card showing gameplay mode
				Player showCardPlayer = players.get(showCardPlayerIdx);
				if(showCardPlayer.getPlayerName().equals(playerId)) {
					// Player must show card
					validActions.add(PlayerActionType.SHOW_CARD);
				}
			}else {
				// Normal gameplay mode
				Player turnPlayer = players.get(turnPlayerIdx);
				if(turnPlayer.getPlayerName().equals(playerId)) {
					// This is the turn player
					if(!turnPlayerMoved) {
						if(turnPlayer.getToken().justTeleported && !turnPlayerSuggested) {
							// Can move if we just teleported and player has not suggested
							validActions.add(PlayerActionType.MOVE);							
						}else {
							// Can move if haven't already
							validActions.add(PlayerActionType.MOVE);							
						}
					}
					if(!turnPlayerSuggested && gameBoard.getIsTokenInRoom(turnPlayer.getToken())) {
						// Can suggest if haven't already and we are in a room
						if(turnPlayer.getToken().previousValid) {	
							// We can only make a suggestion if we have moved
							if(turnPlayer.getToken().getLocationX() != turnPlayer.getToken().getPreviousX() || 
								turnPlayer.getToken().getLocationY() != turnPlayer.getToken().getPreviousY()) {
								validActions.add(PlayerActionType.MAKE_SUGGESTION);								
							}
						}else {
							// Previous location is not valid, we can make a suggestion
							validActions.add(PlayerActionType.MAKE_SUGGESTION);
						}
					}
					// Can always accuse and end turn
					validActions.add(PlayerActionType.MAKE_ACCUSATION);
					validActions.add(PlayerActionType.END_TURN);
				}
			}
		}else if(gameStatus == GameStatus.FINISHED) {
			// Do nothing
		}
		
		// Return the list
		return validActions;
	}
	
	public GameStatus getGameStatus() {
		return gameStatus;
	}
	
	public GameBoard getGameBoard() {
		return gameBoard;
	}
	
	// Get the set of valid move directions
	public List<MoveDirection> getValidMoveDirections(String playerId) {
		if(gameStatus == GameStatus.PLAYING && players.get(turnPlayerIdx).getPlayerName().equals(playerId) && !turnPlayerMoved) {
			// Move directions only if game is in session, is current player's turn, and player has not moved yet
			Token t = players.get(turnPlayerIdx).getToken();
			return gameBoard.getMoveDirections(t);
		}else {
			// Otherwise player cannot move
			return new ArrayList<MoveDirection>();
		}
	}
	
	// Called by engine to skip over players who dont have the right cards
	public List<Card> evaluateCardShowCandidate() {
		// Determine if player actually has any of the cards of interest
		Player showCardPlayer = players.get(showCardPlayerIdx);
		List<Card> playerCards = showCardPlayer.getCards();
		List<Card> showableCards = new ArrayList<Card>();
				
		// See if they have the cards of interest
		for(Card c : playerCards) {
			if(c.getCardType() == CardType.ROOM) {
				RoomCard rc = (RoomCard)c;
				if(rc.getRoomId() == currSuggestion.getRoomId()) {
					showableCards.add(rc);
				}
			}else if(c.getCardType() == CardType.TOKEN) {
				TokenCard tc = (TokenCard)c;
				if(tc.getTokenId() == currSuggestion.getTokenId()) {
					showableCards.add(tc);
				}
			}else if(c.getCardType() == CardType.WEAPON) {
				WeaponCard wc = (WeaponCard)c;
				if(wc.getWeaponId() == currSuggestion.getWeaponId()) {
					showableCards.add(wc);
				}
			}
		}
		
		return showableCards;
	}
	
	// Move the token to one of the valid move directions
	public PlayerActionResult move(String playerId, MoveDirection m) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot move if game is not playing
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot move when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player currPlayer = players.get(turnPlayerIdx);
		if(!currPlayer.getPlayerName().equals(playerId)) {
			// Cannot move when it is not your turn
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot move when it is not your turn");
		}
		
		// Get the set of valid move directions
		List<MoveDirection> validMoveDirections = getValidMoveDirections(playerId);
		
		// Make sure proposed direction is in that list
		if(validMoveDirections.contains(m)) {
			// Move is valid
			Token t = players.get(turnPlayerIdx).getToken();
			gameBoard.moveToken(t,m);
			turnPlayerMoved = true;
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				"Player " + playerId + " successfully moved in direction " + m.toString());
		}else {
			// Move is not valid
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
				"Player " + playerId + " cannot move in direction " + m.toString());
		}
	}
	
	// Make a suggestion
	public PlayerActionResult suggest(String playerId, String tokenId, String weaponId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot suggest if game is not playing
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make a suggestion when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player currPlayer = players.get(turnPlayerIdx);
		if(!currPlayer.getPlayerName().equals(playerId)) {
			// Cannot accuse when it is not your turn
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make a suggestion when it is not your turn");
		}
				
		// Get the room (player's current room)
		Token currPlayerToken = currPlayer.getToken();
		BoardLocation currPlayerLocation = gameBoard.getBoardLocation(currPlayerToken.getLocationX(), currPlayerToken.getLocationY());
		if(currPlayerLocation.getLocationType() != LocationType.ROOM) {
			// Cannot accuse when it is not your turn
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make a suggestion when token is not in a room");
		}
		Room suggestedRoom = (Room)currPlayerLocation;
		
		// Get the token
		List<Token> tokens = gameBoard.getTokens();
		Token suggestedToken = null;
		for(Token t : tokens) {
			if(t.getDisplayName().equals(tokenId)) {
				suggestedToken = t;
			}
		}
		if(suggestedToken == null) {
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make a suggestion with token " + tokenId + " because it does not exist in the game");
		}
		
		// Get the weapon
		List<Weapon> weapons = gameBoard.getWeapons();
		Weapon suggestedWeapon = null;
		for(Weapon w : weapons) {
			if(w.getDisplayName().equals(weaponId)) {
				suggestedWeapon = w;
			}
		}
		if(suggestedWeapon == null) {
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make a suggestion with weapon " + weaponId + " because it does not exist in the game");
		}
		
		// Create the game solution
		GameSolution suggestion = new GameSolution(suggestedRoom.getRoomId(), suggestedToken.getTokenId(), suggestedWeapon.getWeaponId());
		
		// Save the suggestion
		currSuggestion = suggestion;
		
		// Start the process of showing cards
		turnPlayerSuggested = true;
		cardShowingFinished = false;
		showCardPlayerIdx = (turnPlayerIdx+1) % players.size();
		
		// Move the suggested token to the suggested room
		gameBoard.teleportToken(suggestedToken,suggestedRoom);
		
		// Return player result
		return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
				PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				playerId + " suggests " + suggestion);
	}
	
	// Show the card
	public PlayerActionResult showCard(String playerId, String cardType, String cardId) {
		CardType ct = null;
		if(cardType.equals(CardType.ROOM.toString())) {
			ct = CardType.ROOM;
		}else if(cardType.equals(CardType.TOKEN.toString())) {
			ct = CardType.TOKEN;
		}else {
			ct = CardType.WEAPON;
		}

		// Figure out who to show the card to
		Player p = players.get(turnPlayerIdx);

		// We are done showing cards, can continue on with regular game
		cardShowingFinished = true;
		
		// Return player result
		return new PlayerActionResult(GameResultCommunicationType.DIRECTED,p.getPlayerName(),
				PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				playerId + " shows card " + cardId + " (" + ct.toString() + ") to " + p.getPlayerName());
	}
	
	// Make an accusation, wins game if correct, ends turn (and loses) if incorrect
	public PlayerActionResult accuse(String playerId, String roomId, String tokenId, String weaponId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot accuse if game is not playing
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make an accusation when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player currPlayer = players.get(turnPlayerIdx);
		if(!currPlayer.getPlayerName().equals(playerId)) {
			// Cannot accuse when it is not your turn
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
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
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
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
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
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
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot make an accusation with weapon " + weaponId + " because it does not exist in the game");
		}
		
		// Create the game solution
		GameSolution accusation = new GameSolution(accusedRoom.getRoomId(), accusedToken.getTokenId(), accusedWeapon.getWeaponId());
		
		// Check the game solution
		if(gameSolution.equals(accusation)) {
			// Accusation is correct, player wins
			gameStatus = GameStatus.FINISHED;
			winnerIdx = turnPlayerIdx;
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
					"Accusation of " + accusation + " is correct, player " + playerId + " wins!");
			
		}else {
			// Accusation is incorrect, set the player as inactive and end their turn automatically
			Player loser = players.get(turnPlayerIdx);
			loser.setInactive();			
			gameBoard.moveTokenToClosestRoom(loser.getToken());
			endTurn(loser.getPlayerName());

			// Determine if there are enough players for game to continue
			int activeCount = 0;
			int activeIdx = -1;
			for(int i=0; i<players.size(); i++) {
				if(players.get(i).getActive()) {
					activeCount++;
					activeIdx = i;
				}
			}
			
			// Return the correct message
			if(activeCount == 1) {
				// Only one player left, the accuser loses and whoever is left wins by default
				gameStatus = GameStatus.FINISHED;
				winnerIdx = activeIdx;
				return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
						PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
						"Accusation of " + accusation + " is incorrect, player " + playerId + " loses and player " + 
						players.get(activeIdx).getPlayerName() + " wins by default!");
			}else {
				// More than one player left, the accuser loses and the game continues
				return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
						PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
						"Accusation of " + accusation + " is incorrect, player " + playerId + " loses!");
			}
		}
	}
	
	// Ends the current player's turn and readies the next player
	public PlayerActionResult endTurn(String playerId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.PLAYING ) {
			// Cannot accuse if game is not playing
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot end turn when game is not being played");
		}
		
		// Make sure it is the player's turn
		Player p = getPlayer(playerId);
		if(p == null) {
			// Cannot end turn for non existent player
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot end turn for " + playerId + " because that player does not exist in the game");
		}else if(!p.getPlayerName().equals(players.get(turnPlayerIdx).getPlayerName())) {
			// Cannot end turn when it is not your turn
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot end turn for " + playerId + " because that it is not currently that player's turn");
		}
		
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
		cardShowingFinished = false;
		p.getToken().clearJustTeleported();
		
		// Return success
		return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
				PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				"Player " + playerId + " turn has ended");
	}
	
	// Get the game ID
	public String getGameId() {
		return gameId;
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
	public PlayerActionResult addPlayer(String playerId) {
		// Determine if we can take the action
		if(gameStatus != GameStatus.INITIALIZING) {
			// Cannot add players after game has started
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot add any players after game has started");
		}else if(getPlayer(playerId) != null) {
			// Cannot duplicate players
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED, 
					"Cannot create a player with name " + playerId + " since one already exists in the game"); 			
		}else if(players.size() >= MAX_PLAYERS){
			// Too many players already
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Already reached max number of players " + MAX_PLAYERS + ", cannot add any more");
		}else {
			// Add if that player does not exist yet
			Player newPlayer = new Player(playerId);
			players.add(newPlayer);
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
					"Player " + playerId + " has joined the game");
		}
	}
	
	// Remove a player by name
	public PlayerActionResult removePlayer(String playerId) {
		// Determine if we can take the action
		if(gameStatus != GameStatus.INITIALIZING) {
			// Cannot add players after game has started
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot remove any players after game has started");
		}
		
		// Find the player
		int removeIdx = -1;
		for(int i=0; i<players.size(); i++) {
			if(players.get(i).getPlayerName().equals(playerId)) {
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
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
				"Player " + playerId + " has left the game");
		}else {
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot remove player " + playerId + " since no such player exists in the game");
		}	
	}
	
	// Assign token
	public PlayerActionResult assignToken(String playerId, String tokenName) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.INITIALIZING ) {
			// Cannot assign token after game has started
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot assign tokens after game has started");
		}
		
		// Get the player
		Player p = getPlayer(playerId);
		if(p == null) {
			// No player found
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot assign token to player " + playerId + " since no such player exists in the game");
		}
		
		// See if player already has a token
		if(p.hasToken()) {
			// Cannot get another token
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Player " + playerId + " already has a token, must discard the token first before selecting a new one");
		}
		
		// Get the token
		List<Token> tokens = gameBoard.getTokens();
		for(Token t : tokens) {
			if(t.getDisplayName().equals(tokenName)) {
				// Found the token
				if(t.getAvailable()) {
					// Token is available, grab it
					p.assignToken(t);
					return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
							PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
							"Player " + playerId + " has been assigned the token " + tokenName);
				}else {
					// Token is not available
					return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
							PlayerActionResult.ActionResultType.ACTION_REJECTED,
							"The token " + tokenName + " is already taken by another player");
				}
			}
		}
		
		// If we got here then no token was found
		return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
				PlayerActionResult.ActionResultType.ACTION_REJECTED,
				"The token " + tokenName + " does not exist");
	}
	
	// Discard token
	public PlayerActionResult discardToken(String playerId) {
		// Determine if we can take this action
		if(gameStatus != GameStatus.INITIALIZING ) {
			// Cannot assign token after game has started
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot discard tokens after game has started");
		}
		
		// Get the player
		Player p = getPlayer(playerId);
		if(p == null) {
			// No player found
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Cannot discard token of player " + playerId + " since no such player exists in the game");
		}
		
		// See if player already has a token
		if(p.hasToken()) {
			// Remove the token
			Token t = p.getToken();
			p.removeToken();
			return new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
					PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
					"Player " + playerId + " has discarded the token " + t.getDisplayName());			
		}else {
			// Cannot discard a token that doesn't exist
			return new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerId,
					PlayerActionResult.ActionResultType.ACTION_REJECTED,
					"Player " + playerId + " does not have a token to discard");
		}
	}
	
	// Takes existing room, token, and weapon cards
	// Removes one from each to create a game solution and then
	// deals the rest out randomly amongst players
	private void createSolutionAndDealCards() {		
		// Choose a solution randomly
		int roomSolutionIdx = random.nextInt(roomCards.size());
		int tokenSolutionIdx = random.nextInt(tokenCards.size());
		int weaponSolutionIdx = random.nextInt(weaponCards.size());
		gameSolution = new GameSolution(roomCards.get(roomSolutionIdx).getRoomId(),
				tokenCards.get(tokenSolutionIdx).getTokenId(),
				weaponCards.get(weaponSolutionIdx).getWeaponId());
		
		System.out.println("GAME SOLUTION: " + gameSolution);
		
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
}
