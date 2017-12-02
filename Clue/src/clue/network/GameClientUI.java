package clue.network;

import clue.action.PlayerAction;
import clue.result.GameResult;

public interface GameClientUI {

	// Sends a player action to the game server
	void sendPlayerAction(PlayerAction playerAction);
	
	// Receives and handles a result from the game server
	void handleGameResult(GameResult gameResult);
}
