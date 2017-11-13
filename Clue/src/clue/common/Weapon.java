package clue.common;

public class Weapon extends BoardPiece {
	// Enumeration
	public enum WeaponId{
		CANDLESTICK(0),
		KNIFE(1),
		LEAD_PIPE(2),
		REVOLVER(3),
		ROPE(4),
		WRENCH(5);
		
		private final int id;
		WeaponId(int id){
			this.id = id;
		}
		public int getValue() {
			return id;
		}
	}
	
	// Member variables
	protected WeaponId weaponId;
	
	// Constructor
	public Weapon(WeaponId weaponId, String displayName) {
		// Parent constructor
		super(displayName);
				
		// Set member variables
		this.weaponId = weaponId;
	}
	
	// Get methods
	public WeaponId getWeaponId() {
		return weaponId;
	}
}
