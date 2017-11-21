package clue.common;

import java.util.ArrayList;
import java.util.List;

import clue.common.Room.RoomId;

public class RoomCard extends Card {
	// Member variables
	RoomId roomId;
	
	// Constructor
	public RoomCard(RoomId roomId) {
		super(CardType.ROOM);		
		this.roomId = roomId;
	}
	
	public RoomId getRoomId() {
		return roomId;
	}
	
	public String toString() {
		return cardType.toString() + " " + roomId.getDefaultName();
	}
	
	public static List<RoomCard> getCards(){
		List<RoomCard> roomCards = new ArrayList<RoomCard>();
		for(RoomId roomId : RoomId.values()) {
			roomCards.add(new RoomCard(roomId));
		}
		return roomCards;
	}
}
