package clue.ui;

import java.util.Scanner;

import clue.action.PlayerAction;
import clue.action.PlayerActionJoinGame;
import clue.network.GameClientUI;
import clue.result.GameResult;

public class TextUI implements GameClientUI {

	// Client state
	protected enum ClientState{
		CONNECTING,
		JOINING,
		JOINED
	};
	
	// Member variables
	protected String playerId;
	protected ClientState clientState;
	
	// Constructor
	public TextUI() {
		
		// Client should initially be connecting to server
		clientState = ClientState.CONNECTING;

		// Evaluate the state machine
		evaluateStateMachine();
	}
	
	// Evaluates the client state machine to decide what to do 
	private void evaluateStateMachine() {
		switch(clientState) {
		case CONNECTING:
		{
		// Prompt user for a player ID
			System.out.println("Enter a player ID to connect to the server with:");
			Scanner scanner = new Scanner(System.in); 
			playerId = scanner. nextLine();
			scanner.close();
			
			// Connect to server, this step is omitted for now as networking code is still being written
			System.out.println("Successfully connected to server using player ID " + playerId);
			
			// Send request to join game
			PlayerActionJoinGame actionJoinGame = new PlayerActionJoinGame(playerId);
			sendPlayerAction(actionJoinGame);
			break;
		}
		case JOINING:
		{
			// TBD
			break;
		}
		case JOINED:
		{
			// TBD
			break;
		}
		}
	}	
	
	@Override
	public void sendPlayerAction(PlayerAction playerAction) {
		// TBD
	}

	@Override
	public void handleGameResult(GameResult gameResult) {
		switch(gameResult.getGameResultType()){
			case PLAYER_ACTION_RESULT:
			{
				break;
			}
			case GAME_STATE_RESULT:
			{
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
