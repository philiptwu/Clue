package clue.common;

public class Weapon extends BoardPiece {
	// Enumeration
	public enum WeaponId{
		CANDLESTICK(0,"Candlestick"),
		KNIFE(1,"Knife"),
		LEAD_PIPE(2,"Lead Pipe"),
		REVOLVER(3,"Revolver"),
		ROPE(4,"Rope"),
		WRENCH(5,"Wrench");
		
		private final int id;
		private final String defaultName;
		WeaponId(int id, String defaultName){
			this.id = id;
			this.defaultName = defaultName;
		}
		public int getValue() {
			return id;
		}
		public String getDefaultName() {
			return defaultName;
		}
	}
	
	// Member variables
	protected WeaponId weaponId;
	
	// Constructor
	public Weapon(WeaponId weaponId) {
		// Parent constructor
		super(weaponId.getDefaultName());
				
		// Set member variables
		this.weaponId = weaponId;
	}
	
	// Reverse lookup weapon ID by value
	public static WeaponId getWeaponIdByValue(int id) {
		for(WeaponId wid : WeaponId.values()) {
			if(id == wid.getValue()) {
				return wid;
			}
		}
		return null;
	}
	
	// Get methods
	public WeaponId getWeaponId() {
		return weaponId;
	}
}
