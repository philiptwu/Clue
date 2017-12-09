package clue.common;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import clue.action.PlayerAction;
import clue.action.PlayerAction.PlayerActionType;
import clue.action.PlayerActionChooseToken;
import clue.action.PlayerActionDiscardToken;
import clue.action.PlayerActionEndTurn;
import clue.action.PlayerActionJoinGame;
import clue.action.PlayerActionLeaveGame;
import clue.action.PlayerActionMakeAccusation;
import clue.action.PlayerActionMakeSuggestion;
import clue.action.PlayerActionMove;
import clue.action.PlayerActionShowCard;
import clue.common.Game.GameStatus;
import clue.result.PlayerActionResult;
import clue.result.ResultConsumer;
import clue.result.GameResult.GameResultCommunicationType;
import clue.result.GameStateResult;
import clue.result.PlayerActionResult.ActionResultType;
import clue.action.PlayerActionVoteStartGame;

public class GameEngine implements Runnable {
	// Member variables
	protected Game game;
	protected ResultConsumer gameResultConsumer;
	protected Queue<PlayerAction> playerActionQueue;
	
	// Constructor
	public GameEngine(int randomSeed, String gameId, ResultConsumer gameResultConsumer) {
		this.game = new Game(randomSeed,gameId);
		this.gameResultConsumer = gameResultConsumer;
		this.playerActionQueue = new ConcurrentLinkedQueue<PlayerAction>();
	}
	
	// Enqueue a new player action to be handled
	public synchronized void enqueuePlayerAction(PlayerAction pa) {
		playerActionQueue.add(pa);
	}
	
	// Run the engine
	@Override
	public void run() {
		// Infinite loop
		PlayerAction currPlayerAction = null;
		while(true) {
			// If showing cards, skip players who do not need to show cards
			if(game.turnPlayerSuggested && !game.cardShowingFinished) {
				while(true) {
					if(game.showCardPlayerIdx == game.turnPlayerIdx) {
						// Went all the way around, nothing to be shown
						PlayerActionResult nothingResult = new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
								PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
							"No cards were shown by any players in response to the suggestion");
						
						// We are done showing cards
						game.cardShowingFinished = true;
						
						// Handle the action result
						gameResultConsumer.acceptGameResult(game.getGameId(), nothingResult);
						
						// Send the updated game state to each player
						for(Player p : game.players) {
							gameResultConsumer.acceptGameResult(game.getGameId(), new GameStateResult(p.getPlayerName(),game));
						}
						break;
					}
					
					// If we got here then we haven't gone all the way around yet
					List<Card> showableCards = game.evaluateCardShowCandidate();
					if(showableCards.isEmpty()) {
						// Nothing to show
						PlayerActionResult skipResult = new PlayerActionResult(GameResultCommunicationType.BROADCAST,null,
								PlayerActionResult.ActionResultType.ACTION_ACCEPTED,
							"Player " + game.players.get(game.showCardPlayerIdx).getPlayerName() + 
							" did not show any cards in response to the suggestion");

						// Handle the action result
						gameResultConsumer.acceptGameResult(game.getGameId(), skipResult);
						
						// Try the next player
						game.showCardPlayerIdx = (game.showCardPlayerIdx + 1) % game.players.size();
					}else {
						// Something to show
						game.showableCards = showableCards;
						
						// Send the updated game state to each player
						for(Player p : game.players) {
							gameResultConsumer.acceptGameResult(game.getGameId(), new GameStateResult(p.getPlayerName(),game));
						}
						break;
					}
				}
			}
			
			// Wait for a new player action to process
			while(true){
				currPlayerAction = playerActionQueue.poll();
				if(currPlayerAction == null) {
					// Sleep for a second and try again
					try {
					Thread.sleep(500);
					}catch(Exception e) {}
				}else {
					break;
				}
			}
			
			// Process the player action
			PlayerActionResult playerActionResult = processPlayerAction(currPlayerAction);

			// Handle the action result
			gameResultConsumer.acceptGameResult(game.getGameId(), playerActionResult);

			// Don't go any further if the action was rejected
			if(playerActionResult.getPlayerActionResultType() == ActionResultType.ACTION_REJECTED) {
				continue;
			}
			
			// Special actions for game to take if player action was accepted
			GameStatus gameStatus = game.getGameStatus();
			if(gameStatus == GameStatus.INITIALIZING) {
				// INITIALIZING specific
				if(currPlayerAction.getPlayerActionType() == PlayerActionType.LEAVE_GAME || 
						currPlayerAction.getPlayerActionType() == PlayerActionType.VOTE_START_GAME) {
					// Someone left the game or voted to start the game successfully, try to start game
					PlayerActionResult startGameResult = game.startGame();
					System.out.println(startGameResult);
					gameResultConsumer.acceptGameResult(game.getGameId(), startGameResult);
				}
			}	
			
			// Send the updated game state to each player
			for(Player p : game.players) {
				gameResultConsumer.acceptGameResult(game.getGameId(), new GameStateResult(p.getPlayerName(),game));
			}
		}
	}
	
	// Process a player action
	public PlayerActionResult processPlayerAction(PlayerAction playerAction) {
		// Print the action
		System.out.println(playerAction);
		
		// Take the action
		PlayerActionResult actionResult = null;
		switch(playerAction.getPlayerActionType()) {
		case JOIN_GAME:
		{
			// Player wants to join the game
			PlayerActionJoinGame a = (PlayerActionJoinGame)playerAction;
			actionResult = game.addPlayer(a.getPlayerId());
			break;
		}
		case LEAVE_GAME:
		{
			// Player wants to leave the game
			PlayerActionLeaveGame a = (PlayerActionLeaveGame)playerAction;
			actionResult = game.removePlayer(a.getPlayerId());
			break;
		}
		case CHOOSE_TOKEN:
		{
			// Player chooses a token
			PlayerActionChooseToken a = (PlayerActionChooseToken)playerAction;
			actionResult = game.assignToken(a.getPlayerId(), a.getTokenId());
			break;
		}
		case DISCARD_TOKEN:
		{
			// Player discards a token
			PlayerActionDiscardToken a = (PlayerActionDiscardToken)playerAction;
			actionResult = game.discardToken(a.getPlayerId());
			break;
		}
		case VOTE_START_GAME:
		{
			// Start the game
			PlayerActionVoteStartGame a = (PlayerActionVoteStartGame)playerAction;
			actionResult = game.voteStartGame(a.getPlayerId());
			break;
		}
		case MOVE:
		{
			// Move
			PlayerActionMove a = (PlayerActionMove)playerAction;
			actionResult = game.move(a.getPlayerId(), a.getMoveDirection());
			break;
		}
		case MAKE_SUGGESTION:
		{
			// Suggest a solution
			PlayerActionMakeSuggestion a = (PlayerActionMakeSuggestion)playerAction;
			actionResult = game.suggest(a.getPlayerId(), a.getTokenId(), a.getWeaponId());
			break;
		}
		case SHOW_CARD:
		{
			// Show card
			PlayerActionShowCard a = (PlayerActionShowCard)playerAction;
			actionResult = game.showCard(a.getPlayerId(), a.getCardType(), a.getCardId());
			break;
		}
		case MAKE_ACCUSATION:
		{
			// Make an accusation
			PlayerActionMakeAccusation a = (PlayerActionMakeAccusation)playerAction;
			actionResult = game.accuse(a.getPlayerId(), a.getRoomId(), a.getTokenId(), a.getWeaponId());
			break;
		}
		case END_TURN:
		{
			// End turn
			PlayerActionEndTurn a = (PlayerActionEndTurn)playerAction;
			actionResult = game.endTurn(a.getPlayerId());
			break;
		}
		default:
			// Unrecognized action
			actionResult = new PlayerActionResult(GameResultCommunicationType.DIRECTED,playerAction.getPlayerId(),
					ActionResultType.ACTION_REJECTED,
					"Unrecognized player action " + playerAction.getPlayerActionType().toString());
			break;
		}
		
		// Print any return messages
		System.out.println(actionResult);
		
		// Return the status
		return actionResult;
	}
}
