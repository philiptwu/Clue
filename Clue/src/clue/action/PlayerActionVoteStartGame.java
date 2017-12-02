package clue.action;

public class PlayerActionVoteStartGame extends PlayerAction {
	// Member variables
	
	// Constructor
	public PlayerActionVoteStartGame(String playerId) {
		super(playerId, PlayerActionType.VOTE_START_GAME);		
	}
	
	// Get methods
	
	// Output
	public String toString() {
		return "Player " + playerId + " votes to start the game";
	}
}
