package clue.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import clue.action.PlayerAction;
import clue.result.GameResult;

public class GameServerWorker implements Runnable {

	// Member variables
	protected GameServer gameServer;
	protected ObjectInputStream oiStream;
	protected ObjectOutputStream ooStream;
	
	// Constructor
	public GameServerWorker(GameServer gameServer, Socket socket) {
		// Save references
		this.gameServer = gameServer;
		
		// Get streams for writing
		OutputStream oStream = null;
		try {
			oStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ooStream = null;
		try {
			ooStream = new ObjectOutputStream(oStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Get streams for reading
		InputStream iStream = null;
		try {
			iStream = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		oiStream = null;
		try {
			oiStream = new ObjectInputStream(iStream);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	// Sends a game result to the client
	public void sendGameResult(GameResult gameResult) {
		try {
			ooStream.writeObject(gameResult);
			ooStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Listens for incoming player actions and enqueues them
	@Override
	public void run() {
		while(true) {
			try {
				// Get the player action and add to game server queue
				PlayerAction newPlayerAction = (PlayerAction) oiStream.readObject();
				gameServer.getGameEngine().enqueuePlayerAction(newPlayerAction);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
