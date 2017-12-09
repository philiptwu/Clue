package clue.result;

public interface ResultConsumer {
	void acceptGameResult(String gameId, GameResult gameResult);
	
	void printMessage(String message);
	
	void printErrorMessage(String message);
}
