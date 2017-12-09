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
		gameJFrame.updateBoardTextPane(gameStateResult.toString());
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
					printMessage(playerActionResult.toString());
				}else if(playerActionResult.getPlayerActionResultType() == ActionResultType.ACTION_REJECTED) {
					// Print to stder if action was rejected
					printErrorMessage(playerActionResult.toString());
					
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
				// If we are getting game states then we are connected
				if(clientState == ClientState.JOIN_REQUEST_SENT) {
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
				gameJFrame.toggleMovePanel(true);
				gameJFrame.setMoveDirectionButtons(moveMenuDirections);
				break;
			}
			case MAKE_SUGGESTION:
			{
				// First choose a token
				List<String> tokenMenuOptions = new ArrayList<String>();
				for(TokenId ti : TokenId.values()) {
					tokenMenuOptions.add(ti.getDefaultName());
				}
				// Finally choose a weapon
				List<String> weaponMenuOptions = new ArrayList<String>();
				for(WeaponId wi : WeaponId.values()) {
					weaponMenuOptions.add(wi.getDefaultName());
				}
				gameJFrame.setSuggestionComboBox(tokenMenuOptions,weaponMenuOptions);
				gameJFrame.toggleSuggestPanel(true);				
				break;
			}
			case SHOW_CARD:
			{
				// List the available cards
				gameJFrame.setShowableCards(gameStateResult.playerShowableCardTypes,gameStateResult.playerShowableCardIds);
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
	}
	
	// Main
	public static void main(String[] args) {
		// Create a new sample client UI
		new GraphicalUI();
	}

	@Override
	public void printMessage(String message) {
		gameJFrame.printMessage(message);
	}

	@Override
	public void printErrorMessage(String message) {
		gameJFrame.printErrorMessage(message);
	}
}
