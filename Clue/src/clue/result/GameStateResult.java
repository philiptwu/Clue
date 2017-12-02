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
import clue.common.RoomCard;
import clue.common.Token;
import clue.common.TokenCard;
import clue.common.Weapon;
import clue.common.WeaponCard;

public class GameStateResult extends GameResult {
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
}
