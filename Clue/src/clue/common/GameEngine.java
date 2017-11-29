package clue.common;

import clue.action.PlayerAction;
import clue.action.PlayerActionChooseToken;
import clue.action.PlayerActionDiscardToken;
import clue.action.PlayerActionJoinGame;
import clue.action.PlayerActionLeaveGame;
import clue.action.PlayerActionMakeAccusation;
import clue.action.PlayerActionMove;
import clue.action.PlayerActionStartGame;
import clue.status.PlayerStatus;

public class GameEngine {
	// Member variables
	protected Game game;
	
	// Constructor
	public GameEngine(int randomSeed) {
		game = new Game(randomSeed);
	}
	
	// Execute the game engine
	public void execute() {
		// Report game state to player
		
		// 
	}
	
	// Process a player action
	public PlayerStatus processPlayerAction(PlayerAction playerAction) {
		// Print the action
		System.out.println(playerAction);
		
		// Take the action
		PlayerStatus actionStatus = null;
		switch(playerAction.getPlayerActionType()) {
		case JOIN_GAME:
		{
			// Player wants to join the game
			PlayerActionJoinGame a = (PlayerActionJoinGame)playerAction;
			actionStatus = game.addPlayer(a.getPlayerId());
			break;
		}
		case LEAVE_GAME:
		{
			// Player wants to leave the game
			PlayerActionLeaveGame a = (PlayerActionLeaveGame)playerAction;
			actionStatus = game.removePlayer(a.getPlayerId());
			break;
		}
		case CHOOSE_TOKEN:
		{
			// Player chooses a token
			PlayerActionChooseToken a = (PlayerActionChooseToken)playerAction;
			actionStatus = game.assignToken(a.getPlayerId(), a.getTokenId());
			break;
		}
		case DISCARD_TOKEN:
		{
			// Player discards a token
			PlayerActionDiscardToken a = (PlayerActionDiscardToken)playerAction;
			actionStatus = game.discardToken(a.getPlayerId());
			break;
		}
		case START_GAME:
		{
			// Start the game
			PlayerActionStartGame a = (PlayerActionStartGame)playerAction;
			actionStatus = game.startGame();
			break;
		}
		case MOVE:
		{
			// Move
			PlayerActionMove a = (PlayerActionMove)playerAction;
			actionStatus = game.move(a.getPlayerId(), a.getMoveDirection());
			break;
		}
		case MAKE_SUGGESTION:
		{
			// Suggest a solution
			System.err.println("Not yet supported");
			break;
		}
		case SHOW_CARD:
		{
			// Show card
			System.err.println("Not yet supported");
			break;
		}
		case MAKE_ACCUSATION:
		{
			// Make an accusation
			PlayerActionMakeAccusation a = (PlayerActionMakeAccusation)playerAction;
			actionStatus = game.accuse(a.getPlayerId(), a.getRoomId(), a.getTokenId(), a.getWeaponId());
			break;
		}
		default:
			break;
		}
		
		// Print any return messages
		System.out.println(actionStatus);
		
		// Return the status
		return actionStatus;
	}
}
