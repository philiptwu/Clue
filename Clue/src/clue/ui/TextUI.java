package clue.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import clue.action.PlayerAction;
import clue.action.PlayerAction.PlayerActionType;
import clue.action.PlayerActionChooseToken;
import clue.action.PlayerActionDiscardToken;
import clue.action.PlayerActionEndTurn;
import clue.action.PlayerActionJoinGame;
import clue.action.PlayerActionLeaveGame;
import clue.action.PlayerActionMakeAccusation;
import clue.action.PlayerActionMove;
import clue.action.PlayerActionVoteStartGame;
import clue.common.GameBoard;
import clue.common.Room;
import clue.common.GameBoard.MoveDirection;
import clue.common.Room.RoomId;
import clue.common.Token;
import clue.common.Token.TokenId;
import clue.common.Weapon;
import clue.common.Weapon.WeaponId;
import clue.network.GameClient;
import clue.result.GameResult;
import clue.result.GameResult.GameResultCommunicationType;
import clue.result.GameResult.GameResultType;
import clue.result.GameStateResult;
import clue.result.PlayerActionResult;
import clue.result.PlayerActionResult.ActionResultType;
import clue.result.ResultConsumer;

public class TextUI implements ResultConsumer{

	// Client state
	protected enum ClientState{
		UNCONNECTED,
		JOIN_REQUEST_SENT,
		JOINED_GAME,
	};
	
	// Member variables
	protected String playerId;
	protected ClientState clientState;
	protected GameClient gameClient;
	protected GameStateResult gameStateResult;
	
	// Constructor
	public TextUI() {		
		// No latest game state
		gameStateResult = null;
		
		// Client should initially be connecting to server
		clientState = ClientState.UNCONNECTED;
		
		// Create a new client and connect to the server
		gameClient = new GameClient(this);
		
		// Start the game client to receive all incoming GameResult messages
		new Thread(gameClient).start();

		// Join a game
		joinGame();
		
		// Infinite loop
		while(true) {
			try {
			Thread.sleep(10000);
			}catch(Exception e) {
				
			}
		}
	}
	
	// Evaluates the client state machine to decide what to do 
	synchronized private void joinGame() {
		if(clientState == ClientState.UNCONNECTED) {
			
			
			// Prompt user for a player ID
			System.out.println("Enter a player ID to join game with:");
			Scanner scanner = new Scanner(System.in); 
			playerId = scanner.nextLine();
			scanner.close();
			
			// Send request to join game
			PlayerActionJoinGame actionJoinGame = new PlayerActionJoinGame(playerId);
			gameClient.sendPlayerAction(actionJoinGame);
			
			// Transition the state
			clientState = ClientState.JOIN_REQUEST_SENT;
		}
	}	
	
	// Update visualization of the game state
	synchronized public void displayGameState(GameStateResult gameStateResult) {
		System.out.println("***********************************************");
		System.out.println(gameStateResult);
		System.out.println("***********************************************");
	}

	// Called by client 
	@Override
	synchronized public void acceptGameResult(String gameId, GameResult gameResult) {
		// Filter the message
		switch(clientState) {
		case UNCONNECTED:
		{
			// We didn't even connect yet, this game result is not for us
			return;			
		}
		case JOIN_REQUEST_SENT:
		{
			// Make sure it is a directed palyer action result meant for us
			if(gameResult.getGameResultType() == GameResultType.PLAYER_ACTION_RESULT && 
			gameResult.getGameResultCommunicationType() == GameResultCommunicationType.DIRECTED && 
			gameResult.getPlayerId().equals(playerId)) {
				// DO nothing
			}else {
				// We sent a join request, only care about player action results for us
				return;
			}
		}
		case JOINED_GAME:
		{
			if(gameResult.getGameResultCommunicationType() == GameResultCommunicationType.BROADCAST || 
					gameResult.getPlayerId().equals(playerId)) {
				// DO nothing
			}else {
				// Not for us, filter it out
				return;
			}
		}
		}
		
		switch(gameResult.getGameResultType()){
			case PLAYER_ACTION_RESULT:
			{
				// Add new player action result to queue to be processed
				PlayerActionResult playerActionResult = (PlayerActionResult)gameResult;
				if(playerActionResult.getPlayerActionResultType() == ActionResultType.ACTION_ACCEPTED) {
					// Print to stdout if action was accepted
					System.out.println(playerActionResult);
				}else if(playerActionResult.getPlayerActionResultType() == ActionResultType.ACTION_REJECTED) {
					// Print to stder if action was rejected
					System.err.println(playerActionResult);
					
					// If we got a rejection after sending a join request, then it failed so try again
					if(clientState == ClientState.JOIN_REQUEST_SENT) {
						clientState = ClientState.UNCONNECTED;
						joinGame();
					}
				}
				break;
			}
			case GAME_STATE_RESULT:
			{
				// If we are getting game states then we are connected
				clientState = ClientState.JOINED_GAME;

				// Save the latest game state result and indicate that display should be updated
				gameStateResult = (GameStateResult)gameResult;
					
				// Display the new game state
				displayGameState(gameStateResult);
				
				// Accept any user input
				acceptUserInput(gameStateResult);
				break;
			}
		}
	}
	
	// Gets user input and sends player action
	synchronized public void acceptUserInput(GameStateResult gameStateResult) {
		// Nothing to do if player has no actions to take
		if(gameStateResult.validActions.isEmpty()) {
			return;
		}
		
		// Create a new scanner
		Scanner scanner = new Scanner(System.in); 
		
		// There is at least one action that the player can take, show the options
		List<String> actionMenuOptions = new ArrayList<String>();
		for(PlayerActionType pat : gameStateResult.validActions) {
			actionMenuOptions.add(pat.toString());
		}
		int actionSelectionIdx = getNumberMenu(scanner,"Input an Action Number:",actionMenuOptions);
		
		// Follow up on which action was selected
		PlayerActionType selectedActionType = gameStateResult.validActions.get(actionSelectionIdx);
		
		// Follow up on which action was selected
		PlayerAction actionToSend = null;
		switch(selectedActionType) {
		case JOIN_GAME:
		{
			// Join a game (this should not be used)
			actionToSend = new PlayerActionJoinGame(playerId);
			break;
		}
		case LEAVE_GAME:
		{
			// Leave the current game
			actionToSend = new PlayerActionLeaveGame(playerId);
			break;
		}
		case CHOOSE_TOKEN:
		{
			// Choose from one of the available tokens
			List<String> tokenMenuOptions = new ArrayList<String>();
			for(int i=0; i<TokenId.values().length; i++) {
				if(gameStateResult.tokenAvailable[i]) {
					TokenId ti = Token.getTokenIdByValue(i);
					tokenMenuOptions.add(ti.getDefaultName());
				}
			}
			int tokenSelectionIdx = getNumberMenu(scanner,"Input a Token Selection Number:",tokenMenuOptions);
			actionToSend = new PlayerActionChooseToken(playerId,tokenMenuOptions.get(tokenSelectionIdx));
			break;
		}
		case DISCARD_TOKEN:
		{
			// Discard token
			actionToSend = new PlayerActionDiscardToken(playerId);
			break;
		}
		case VOTE_START_GAME:
		{
			// Vote to start the game
			actionToSend = new PlayerActionVoteStartGame(playerId);
			break;
		}
		case MOVE:
		{
			// Move the token
			// First show list of valid moves
			List<String> moveMenuOptions = new ArrayList<String>();
			List<MoveDirection> moveMenuDirections = new ArrayList<MoveDirection>();
			for(int i=0; i<MoveDirection.values().length; i++) {
				if(gameStateResult.moveDirectionValid[i]) {
					MoveDirection md = GameBoard.getMoveDirectionByValue(i);
					moveMenuOptions.add(md.toString());
					moveMenuDirections.add(md);
				}
			}
			int moveSelectionIdx = getNumberMenu(scanner,"Input a Move Direction Number:",moveMenuOptions);
			MoveDirection selectedMoveDirection = moveMenuDirections.get(moveSelectionIdx);
			
			// Send the action
			actionToSend = new PlayerActionMove(playerId, selectedMoveDirection);
			break;
		}
		case MAKE_SUGGESTION:
		{
			// Make a suggestion
			System.err.println("Make Accusation action is currently unsupported");
			break;
		}
		case SHOW_CARD:
		{
			// Show a card (in response to a suggestion)
			System.err.println("Show Card action is currently unsupported");
			break;
		}
		case MAKE_ACCUSATION:
		{
			// Make an accusation
			// First choose a room
			List<String> roomMenuOptions = new ArrayList<String>();
			for(RoomId ri : RoomId.values()) {
				roomMenuOptions.add(ri.getDefaultName());
			}
			int roomSelectionIdx = getNumberMenu(scanner,"Input a Room Selection Number:",roomMenuOptions);
			// Next choose a token
			List<String> tokenMenuOptions = new ArrayList<String>();
			for(TokenId ti : TokenId.values()) {
				tokenMenuOptions.add(ti.getDefaultName());
			}
			int tokenSelectionIdx = getNumberMenu(scanner,"Input a Token Selection Number:",tokenMenuOptions);
			// Finally choose a weapon
			List<String> weaponMenuOptions = new ArrayList<String>();
			for(WeaponId wi : WeaponId.values()) {
				weaponMenuOptions.add(wi.getDefaultName());
			}
			int weaponSelectionIdx = getNumberMenu(scanner,"Input a Weapon Selection Number:",weaponMenuOptions);
			// Create the action
			actionToSend = new PlayerActionMakeAccusation(playerId,
					Room.getRoomIdByValue(roomSelectionIdx).getDefaultName(),
					Token.getTokenIdByValue(tokenSelectionIdx).getDefaultName(),
					Weapon.getWeaponIdByValue(weaponSelectionIdx).getDefaultName());			
			break;
		}
		case END_TURN:
		{
			// End turn
			actionToSend = new PlayerActionEndTurn(playerId);
			break;
		}
		default:
		{
			// Unrecognized action type
			System.err.println("Player selected an unsupported action type " + selectedActionType.toString());
			return;
		}
		}
		
		// Send the selected action
		gameClient.sendPlayerAction(actionToSend);
	}
		
	// Display a numerical menu to the user and get input
	synchronized private int getNumberMenu(Scanner scanner, String header, List<String> menuOptions) {
		while(true) {
			// Display header
			System.out.println(header);
			
			// Display menu
			int numOptions = menuOptions.size();
			for(int i=1; i<=numOptions; i++) {
				System.out.println(i + ") " + menuOptions.get(i));
			}
			
			// Get input
			try {
				// Parse the integer, a NumberFormatException will be thrown if it is not valid
				int actionSelection = Integer.parseInt(scanner.nextLine());

				// Check input validity
				if(actionSelection < 1) {
					// Too small
					System.err.println("Input must be >= 1, try again...\n");
				}else if(actionSelection > numOptions) {
					// Too large
					System.err.println("Input must be <= " + numOptions + ", try again...\n");
				}else {
					// Just right
					return (actionSelection-1);
				}

			}catch(NumberFormatException e) {
				// Invalid number
				System.err.println("Input is not a valid number, try again...\n");
			}
		}
	}
	
	// Main
	public static void main(String[] args) {
		// Create a new sample client UI
		new TextUI();
	}
}
