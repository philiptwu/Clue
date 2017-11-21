package clue.common;

import clue.common.Room.RoomId;
import clue.common.Token.TokenId;
import clue.common.Weapon.WeaponId;

public class GameSolution {
	// Member variables
	protected RoomId roomId;
	protected TokenId tokenId;
	protected WeaponId weaponId;
	
	// Constructor
	public GameSolution(RoomId roomId, TokenId tokenId, WeaponId weaponId) {
		this.roomId = roomId;
		this.tokenId = tokenId;
		this.weaponId = weaponId;
	}
	
	// Get methods
	public RoomId getRoomId() {
		return roomId;
	}
	public TokenId getTokenId() {
		return tokenId;
	}
	public WeaponId getWeaponId() {
		return weaponId;
	}

	// Compare
	public boolean equals(Object o) {
		GameSolution g = (GameSolution) o;
		return roomId.equals(g.roomId) && tokenId.equals(g.tokenId) && weaponId.equals(g.weaponId);
	}
	
	// Debug
	public String toString() {
		return roomId.getDefaultName() + ", " + tokenId.getDefaultName() + ", " + weaponId.getDefaultName();
	}
}
