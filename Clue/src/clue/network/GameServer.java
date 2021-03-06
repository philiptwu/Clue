package clue.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import clue.common.GameEngine;
import clue.result.GameResult;
import clue.result.ResultConsumer;

public class GameServer implements ResultConsumer {
	// Member variables
	static final int PORT = 2017;
	protected GameEngine gameEngine;
	protected List<GameServerWorker> gameServerWorkers;
	
	// Constructor
	@SuppressWarnings("resource")
	public GameServer() {
		// Debug output
		int randomSeed = (int)new Date().getTime();
		printMessage("Starting game server with random seed " + randomSeed + "...");
		
		// List of game server workers
		gameServerWorkers = new ArrayList<GameServerWorker>();
		
		// Create a new game engine
		gameEngine = new GameEngine(randomSeed,"test",this);
		
		// Start a game engine thread
		Thread gameEngineThread = new Thread(gameEngine);
		gameEngineThread.start();
		
		// Create socket
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		// Open a socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printMessage("Game server started!");
        
        // Infinite loop
        while (true) {
            try {
            	// Mother socket accepts inbound connection
            	printMessage("Waiting for client connection request...");
                socket = serverSocket.accept();
            } catch (IOException e) {
            	printErrorMessage("I/O error: " + e);
            }
            
            // Spawns off a new socket and game server worker for each client
            printMessage("New client connected!");
            GameServerWorker gameServerWorker = new GameServerWorker(this,socket);
            Thread gameServerWorkerThread = new Thread(gameServerWorker);
            gameServerWorkerThread.start();
            gameServerWorkers.add(gameServerWorker);
        }
	}

	public GameEngine getGameEngine() {
		return gameEngine;
	}
	
	@Override
	public void acceptGameResult(String gameId, GameResult gameResult) {
		// TODO Auto-generated method stub
		for(GameServerWorker gsw : gameServerWorkers) {
			gsw.sendGameResult(gameResult);
		}
	}
	
	// Main function
	public static void main(String[] args) {
		new GameServer();
	}

	@Override
	public void printMessage(String message) {
		System.out.println(message);		
	}

	@Override
	public void printErrorMessage(String message) {
		System.err.println(message);
	}
}
