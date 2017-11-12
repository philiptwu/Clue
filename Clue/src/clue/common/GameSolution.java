package clue.common;

import clue.common.Room.RoomId;
import clue.common.Token.TokenId;
import clue.common.Weapon.WeaponId;

public class GameSolution {
	// Member variables
	TokenId tokenId;
	WeaponId weaponId;
	RoomId roomId;
	
	// Constructor
	public GameSolution(TokenId tokenId, WeaponId weaponId, RoomId roomId) {
		this.tokenId = tokenId;
		this.weaponId = weaponId;
		this.roomId = roomId;
	}
	
	// Get methods
	public TokenId getTokenId() {
		return tokenId;
	}
	public WeaponId getWeaponId() {
		return weaponId;
	}
	public RoomId getRoomId() {
		return roomId;
	}
}
