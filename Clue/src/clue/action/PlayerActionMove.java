package clue.action;

import clue.common.GameBoard.MoveDirection;

public class PlayerActionMove extends PlayerAction {
	// Member variables
	MoveDirection moveDirection;
	
	// Constructor
	public PlayerActionMove(String playerId, MoveDirection moveDirection) {
		super(playerId, PlayerActionType.MOVE);		
		this.moveDirection = moveDirection;
	}
	
	// Get methods
	public MoveDirection getMoveDirection() {
		return moveDirection;
	}
	
	// Output
	public String toString() {
		return "Player " + playerId + " requesting to move in direction " + moveDirection.toString();
	}
}
