package clue.network;

import clue.common.GameEngine;
import clue.result.GameResult;
import clue.result.ResultConsumer;

public class GameServer implements ResultConsumer {
	// Member variables
	protected GameEngine gameEngine;
	
	// Constructor
	public GameServer() {
		// Create a new game engine
		gameEngine = new GameEngine(0,"test",this);
		
		
	}

	@Override
	public void acceptGameResult(String gameId, GameResult gameResult) {
		// TODO Auto-generated method stub
		
	}


}
