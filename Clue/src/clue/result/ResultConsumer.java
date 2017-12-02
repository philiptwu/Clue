package clue.result;

public interface ResultConsumer {
	void acceptGameResult(String gameId, GameResult gameResult);
}
