package clue.common;

import java.util.ArrayList;
import java.util.List;

import clue.common.Weapon.WeaponId;

public class WeaponCard extends Card {
	// Member variables
	WeaponId weaponId;
	
	// Constructor
	public WeaponCard(WeaponId weaponId) {
		super(CardType.WEAPON);		
		this.weaponId = weaponId;
	}
	
	public WeaponId getWeaponId() {
		return weaponId;
	}
	
	public String toString() {
		return cardType.toString() + " " + weaponId.getDefaultName();
	}
	
	public static List<WeaponCard> getCards(){
		List<WeaponCard> weaponCards = new ArrayList<WeaponCard>();
		for(WeaponId weaponId : WeaponId.values()) {
			weaponCards.add(new WeaponCard(weaponId));
		}
		return weaponCards;
	}
}
