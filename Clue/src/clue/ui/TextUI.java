package clue.ui;

import java.util.Scanner;

import clue.action.PlayerActionJoinGame;
import clue.network.GameClient;
import clue.result.GameResult;
import clue.result.GameStateResult;
import clue.result.PlayerActionResult;
import clue.result.PlayerActionResult.ActionResultType;

public class TextUI{

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
		// Create a new client
		gameClient = new GameClient();
		
		// No latest game state
		gameStateResult = null;
		
		// Client should initially be connecting to server
		clientState = ClientState.UNCONNECTED;
		
		// Connect to the server and game
		connect();
		
		// Infinite loop
		while(true) {
			try {
			Thread.sleep(1000);
			}catch(Exception e) {
				
			}
		}
	}
	
	// Evaluates the client state machine to decide what to do 
	synchronized private void connect() {
		if(clientState == ClientState.UNCONNECTED) {
			// Prompt user for a player ID
			System.out.println("Enter a player ID to connect to the server with:");
			Scanner scanner = new Scanner(System.in); 
			playerId = scanner. nextLine();
			scanner.close();
			
			// Connect to server, this step is omitted for now as networking code is still being written
			System.out.println("Successfully connected to server using player ID " + playerId);
			
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
	synchronized public void handleGameResult(GameResult gameResult) {
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
						connect();
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
				
				break;
			}
		}
	}
	
	// Main
	public static void main(String[] args) {
		// Create a new sample client UI
		new TextUI();
	}
}
