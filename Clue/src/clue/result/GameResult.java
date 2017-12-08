package clue.result;

import java.io.Serializable;

public abstract class GameResult implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8087787907305037542L;
	public enum GameResultType{
		PLAYER_ACTION_RESULT(0,"Player Action Result"),
		GAME_STATE_RESULT(1,"Game State Result");
		
		private final int id;
		private final String displayName;
		GameResultType(int id, String displayName){
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
	
	public enum GameResultCommunicationType{
		DIRECTED,
		BROADCAST;
	}
	
	// Member variables
	protected GameResultCommunicationType gameResultCommunicationType;
	protected GameResultType gameResultType;
	protected String playerId;
	
	// Constructor
	public GameResult(GameResultType gameResultType, 
			GameResultCommunicationType gameResultCommunicationType, 
			String playerId) {
		this.gameResultType = gameResultType;
		this.gameResultCommunicationType = gameResultCommunicationType;
		this.playerId = playerId;
	}
		
	// Get methods
	public GameResultType getGameResultType() {
		return gameResultType;
	}
	public String getPlayerId() {
		return playerId;
	}
	public GameResultCommunicationType getGameResultCommunicationType() {
		return gameResultCommunicationType;
	}
	
}
