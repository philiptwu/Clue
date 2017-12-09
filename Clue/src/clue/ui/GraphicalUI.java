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
import clue.gui.GameJFrame;
import clue.network.GameClient;
import clue.result.GameResult;
import clue.result.GameResult.GameResultCommunicationType;
import clue.result.GameResult.GameResultType;
import clue.result.GameStateResult;
import clue.result.PlayerActionResult;
import clue.result.PlayerActionResult.ActionResultType;
import clue.result.ResultConsumer;

public class GraphicalUI implements ResultConsumer{

	// Client state
	protected enum ClientState{
		UNCONNECTED,
		JOIN_REQUEST_SENT,
		JOINED_GAME,
	};
	
	// Member variables
	protected String playerId;
	protected ClientState clientState;
	public GameClient gameClient;
	public GameStateResult gameStateResult;
	protected GameJFrame gameJFrame;
	
	// Constructor
	public GraphicalUI() {		
		// No latest game state
		gameStateResult = null;
		
		// Client should initially be connecting to server
		clientState = ClientState.UNCONNECTED;

		// Create a Game JFrame
		gameJFrame = new GameJFrame(this);
		gameJFrame.setVisible(true);
	}
	
	public void connectToServer(String ipAddress, int port) {
		// Create a new client and connect to the server
		gameClient = new GameClient(this, ipAddress, port);
		
		// Start the game client to receive all incoming GameResult messages
		new Thread(gameClient).start();
		
		// Enable the join game panel
		gameJFrame.toggleJoinGamePanel(true);
	}
	
	public String getPlayerId() {
		return playerId;
	}
		
	// Evaluates the client state machine to decide what to do 
	synchronized public void joinGame(String playerId) {
		if(clientState == ClientState.UNCONNECTED) {					
			// Send request to join game
			this.playerId = playerId;
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
		System.out.println("RECEIVED A GAME RESULT!");
		System.out.println(gameResult);
		System.out.println("Player ID is " + playerId);
		
		// Filter the message
		switch(clientState) {
		case UNCONNECTED:
		{
			// We didn't even connect yet, this game result is not for us
			return;			
		}
		case JOIN_REQUEST_SENT:
		{
			// Make sure it is a directed player action result meant for us
			if(gameResult.getGameResultCommunicationType() == GameResultCommunicationType.BROADCAST || 
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
						gameJFrame.toggleJoinGamePanel(true);
					}
				}
				break;
			}
			case GAME_STATE_RESULT:
			{				
				System.out.println("HERE");
				// If we are getting game states then we are connected
				if(clientState == ClientState.JOIN_REQUEST_SENT) {
					System.out.println("AND HERE, LOOKING FOR " + playerId);
					// Look for game state result with player's name
					for(String pid : ((GameStateResult)gameResult).playerIds) {
						if(playerId.equals(pid)) {
							clientState = ClientState.JOINED_GAME;
							break;
						}
					}
					
					// Don't show the game state if we haven't joined yet
					if(clientState != ClientState.JOINED_GAME) {
						return;
					}
				}

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
		// Clear all first
		gameJFrame.disableAllMenuPanels();
		
		// Nothing to do if player has no actions to take
		if(gameStateResult.validActions.isEmpty()) {
			return;
		}
		
		// Show the action panels
		for(PlayerActionType pat : gameStateResult.validActions) {
			switch(pat) {
			case JOIN_GAME:
			{
				break;
			}
			case LEAVE_GAME:
			{
				break;
			}
			case CHOOSE_TOKEN:
			{
				List<String> tokenMenuOptions = new ArrayList<String>();
				for(int i=0; i<TokenId.values().length; i++) {
					if(gameStateResult.tokenAvailable[i]) {
						TokenId ti = Token.getTokenIdByValue(i);
						tokenMenuOptions.add(ti.getDefaultName());
					}
				}
				gameJFrame.setChooseTokenComboBox(tokenMenuOptions);
				gameJFrame.toggleChooseTokenPanel(true);
				break;
			}
			case DISCARD_TOKEN:
			{
				gameJFrame.toggleDiscardTokenPanel(true);
				break;
			}
			case VOTE_START_GAME:
			{
				gameJFrame.toggleStartGamePanel(true);
				break;
			}
			case MOVE:
			{
				List<MoveDirection> moveMenuDirections = new ArrayList<MoveDirection>();
				for(int i=0; i<MoveDirection.values().length; i++) {
					if(gameStateResult.moveDirectionValid[i]) {
						MoveDirection md = GameBoard.getMoveDirectionByValue(i);
						moveMenuDirections.add(md);
					}
				}
				gameJFrame.setMoveDirectionButtons(moveMenuDirections);
				gameJFrame.toggleMovePanel(true);
				break;
			}
			case MAKE_SUGGESTION:
			{
				printErrorMessage("Make Suggestion action is currently not supported");
				gameJFrame.toggleSuggestPanel(true);
				break;
			}
			case SHOW_CARD:
			{
				printErrorMessage("Show Card action is currently not supported");
				gameJFrame.toggleShowCardPanel(true);
				break;
			}
			case MAKE_ACCUSATION:
			{
				// First choose a room
				List<String> roomMenuOptions = new ArrayList<String>();
				for(RoomId ri : RoomId.values()) {
					roomMenuOptions.add(ri.getDefaultName());
				}
				// Next choose a token
				List<String> tokenMenuOptions = new ArrayList<String>();
				for(TokenId ti : TokenId.values()) {
					tokenMenuOptions.add(ti.getDefaultName());
				}
				// Finally choose a weapon
				List<String> weaponMenuOptions = new ArrayList<String>();
				for(WeaponId wi : WeaponId.values()) {
					weaponMenuOptions.add(wi.getDefaultName());
				}
				gameJFrame.setAccusationComboBox(roomMenuOptions,tokenMenuOptions,weaponMenuOptions);
				gameJFrame.toggleAccusePanel(true);
				break;
			}
			case END_TURN:
			{
				gameJFrame.toggleEndTurnPanel(true);
				break;
			}
			default:
			{
				break;
			}
			}
		}
		/*
		// There is at least one action that the player can take, show the options
		List<String> actionMenuOptions = new ArrayList<String>();
		for(PlayerActionType pat : gameStateResult.validActions) {
			actionMenuOptions.add(pat.toString());
		}
		int actionSelectionIdx = getNumberMenu(scanner,"Available Actions...",actionMenuOptions);
		
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
			int tokenSelectionIdx = getNumberMenu(scanner,"Available Tokens...",tokenMenuOptions);
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
			int moveSelectionIdx = getNumberMenu(scanner,"Available Move Directions...",moveMenuOptions);
			MoveDirection selectedMoveDirection = moveMenuDirections.get(moveSelectionIdx);
			
			// Send the action
			actionToSend = new PlayerActionMove(playerId, selectedMoveDirection);
			break;
		}
		case MAKE_SUGGESTION:
		{
			// Make a suggestion
			System.err.println("Make Suggestion action is currently unsupported");
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
			int roomSelectionIdx = getNumberMenu(scanner,"Available Rooms...",roomMenuOptions);
			// Next choose a token
			List<String> tokenMenuOptions = new ArrayList<String>();
			for(TokenId ti : TokenId.values()) {
				tokenMenuOptions.add(ti.getDefaultName());
			}
			int tokenSelectionIdx = getNumberMenu(scanner,"Available Tokens...",tokenMenuOptions);
			// Finally choose a weapon
			List<String> weaponMenuOptions = new ArrayList<String>();
			for(WeaponId wi : WeaponId.values()) {
				weaponMenuOptions.add(wi.getDefaultName());
			}
			int weaponSelectionIdx = getNumberMenu(scanner,"Available Weapons...",weaponMenuOptions);
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
		*/
	}
	
	// Main
	public static void main(String[] args) {
		// Create a new sample client UI
		new GraphicalUI();
	}

	@Override
	public void printMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void printErrorMessage(String message) {
		// TODO Auto-generated method stub
		
	}
}
