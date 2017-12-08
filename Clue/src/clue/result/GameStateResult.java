package clue.result;

import java.util.ArrayList;
import java.util.List;

import clue.action.PlayerAction.PlayerActionType;
import clue.common.Card;
import clue.common.Card.CardType;
import clue.common.Game;
import clue.common.Game.GameStatus;
import clue.common.GameBoard.MoveDirection;
import clue.common.Player;
import clue.common.Room;
import clue.common.RoomCard;
import clue.common.Token;
import clue.common.Token.TokenId;
import clue.common.TokenCard;
import clue.common.Weapon;
import clue.common.Weapon.WeaponId;
import clue.common.WeaponCard;

public class GameStateResult extends GameResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3719047721815744892L;
	// Common member variables
	protected String gameId;
	public int turnPlayerIdx;
	public boolean turnPlayerMoved;
	public boolean turnPlayerSuggested;
	public int winnerIdx;
	public GameStatus gameStatus;
	public int[] weaponLocationX;
	public int[] weaponLocationY;
	public int[] tokenLocationX;
	public int[] tokenLocationY;
	public boolean[] tokenAvailable;
	public List<String> playerIds;
	public List<Boolean> playerActive;
	public List<Boolean> playerVoted;
	public List<Integer> playerTokenAssociation;
	
	// Player-specific variables
	public List<Integer> playerCardTypes;
	public List<Integer> playerCardIds;
	public List<PlayerActionType> validActions;
	public boolean[] moveDirectionValid;
	
	// Constructor
	public GameStateResult(String playerId, Game game) {
		super(GameResultType.GAME_STATE_RESULT, 
			GameResultCommunicationType.DIRECTED,
			playerId);

		////////////////////////////////////////////////////
		// Common
		////////////////////////////////////////////////////
		
		// Copy variables over
		this.gameId = game.gameId;
		this.turnPlayerIdx = game.turnPlayerIdx;
		this.turnPlayerMoved = game.turnPlayerMoved;
		this.turnPlayerSuggested = game.turnPlayerSuggested;
		this.winnerIdx = game.winnerIdx;
		this.gameStatus = game.gameStatus;
		
		// Weapon locations
		weaponLocationX = new int[6];
		weaponLocationY = new int[6];
		for(Weapon w : game.getGameBoard().getWeapons()) {
			int idx = w.getWeaponId().getValue();
			weaponLocationX[idx] = w.getLocationX();
			weaponLocationY[idx] = w.getLocationY();
		}
		
		// Token locations and availability
		tokenLocationX = new int[6];
		tokenLocationY = new int[6];
		tokenAvailable = new boolean[6];
		for(Token t : game.getGameBoard().getTokens()) {
			int idx = t.getTokenId().getValue();
			tokenLocationX[idx] = t.getLocationX();
			tokenLocationY[idx] = t.getLocationY();
			tokenAvailable[idx] = t.getAvailable();
		}
		
		// Player information
		playerIds = new ArrayList<String>();
		playerActive = new ArrayList<Boolean>();
		playerVoted = new ArrayList<Boolean>();
		playerTokenAssociation = new ArrayList<Integer>();
		for(Player p : game.players) {
			playerIds.add(p.getPlayerName());
			playerActive.add(p.getActive());
			playerVoted.add(p.getVoted());
			
			// Player token selection
			if(p.hasToken()) {
				playerTokenAssociation.add(p.getToken().getTokenId().getValue());
			}else {
				playerTokenAssociation.add(-1);
			}
		}
		
		////////////////////////////////////////////////////
		// Player-Specific
		////////////////////////////////////////////////////
		
		// Player cards
		playerCardTypes = new ArrayList<Integer>();
		playerCardIds = new ArrayList<Integer>();
		for(Card c : game.getPlayer(playerId).getCards()) {
			CardType ct = c.getCardType();
			playerCardTypes.add(ct.getValue());
			if(ct == CardType.ROOM) {
				playerCardIds.add(((RoomCard)c).getRoomId().getValue());
			}else if(ct == CardType.TOKEN) {
				playerCardIds.add(((TokenCard)c).getTokenId().getValue());			
			}else if(ct == CardType.WEAPON) {
				playerCardIds.add(((WeaponCard)c).getWeaponId().getValue());							
			}
		}
		
		// Valid actions
		validActions = new ArrayList<PlayerActionType>(game.getValidPlayerActions(playerId));
		
		// Valid move directions
		for(MoveDirection md : game.getValidMoveDirections(playerId)) {
			moveDirectionValid[md.getValue()] = true;
		}
	}
	
	// String representation of game state
	public String toString() {
		String str = new String();
		
		// Game basic information
		str += "Game Status: " + gameStatus.toString() + "\n";
		
		// Weapons
		str += "Weapons:\n";
		for(int i=0; i<6; i++) {
			WeaponId wid = Weapon.getWeaponIdByValue(i);
			str += "  " + wid.getDefaultName() + ", Location = (" + weaponLocationX[i] + "," + weaponLocationY[i] + ")\n";
		}
		
		// Tokens
		str += "Tokens:\n";
		for(int i=0; i<6; i++) {
			TokenId tid = Token.getTokenIdByValue(i);
			if(gameStatus == GameStatus.INITIALIZING) {
				// Game is initializing, we care about whether a token is available
				str += "  " + tid.getDefaultName() + ", Location = (" + tokenLocationX[i] + "," + tokenLocationY[i] + "), Available = " + tokenAvailable[i] + "\n";
			}else {
				// Game is playing or done, don't care if token is available
				str += "  " + tid.getDefaultName() + ", Location = (" + tokenLocationX[i] + "," + tokenLocationY[i] + ")\n";
			}
		}
				
		// Players
		str += "Players:\n";
		int numPlayers = playerIds.size();
		for(int i=0; i<numPlayers; i++) {
			// We will always want the player name
			String playerId = playerIds.get(i);
			
			// We also always want to know which token
			int playerToken = playerTokenAssociation.get(i);
			String tokenId = (playerToken >= 0) ? Token.getTokenIdByValue(playerToken).getDefaultName() : "None";
			
			// Create the appropriate output string
			if(gameStatus == GameStatus.INITIALIZING) {
				// Still initializing, status and turn does not matter
				str += "  " + playerId + ", Token = " + tokenId + ", Voted = " + playerVoted.get(i) + "\n";
			}else if(gameStatus == GameStatus.PLAYING) {				
				// No one won yet
				String status = null;
				if(playerActive.get(i)) {
					// Active
					if(i == turnPlayerIdx) {
						status = "Turn";
					}else {
						status = "Not Turn";
					}
				}else {
					// All inactive players are losers
					status = "Loser";
				}
				str += "  " + playerId + ", Token = " + tokenId + ", Status = " + status + "\n";
			}else {
				// Someone won, everyone who is not the winner is a loser
				String status = (i == winnerIdx) ? "Winner" : "Loser";				
				str += "  " + playerId + ", Token = " + tokenId + ", Status = " + status + "\n";
			}
		}
		
		// Player specific
		str += "Current Player:\n";
		if(gameStatus == GameStatus.INITIALIZING) {
			// Players have no cards when initializing
			str += "  " + playerId + ", Cards = None\n";			
		}else {
			// Players have cards if game is past initializing
			str += "  " + playerId + ", Cards = ";
			for(int i=0; i<playerCardTypes.size(); i++) {
				if(i > 0) {
					str += ", ";
				}
				CardType ct = Card.getCardTypeByValue(playerCardTypes.get(i));
				switch(ct) {
				case ROOM:
					str += Room.getRoomIdByValue(playerCardIds.get(i)).getDefaultName() + " (Room)";
					break;
				case TOKEN:
					str += Token.getTokenIdByValue(playerCardIds.get(i)).getDefaultName() + " (Token)";					
					break;
				case WEAPON:
					str += Weapon.getWeaponIdByValue(playerCardIds.get(i)).getDefaultName() + " (Weapon)";										
					break;
				}
			}
			str += "\n";
		}
		
		// Return the created string
		return str;
	}
}
