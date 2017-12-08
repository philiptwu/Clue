package clue.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import clue.action.PlayerAction;
import clue.result.GameResult;
import clue.result.ResultConsumer;

public class GameClient implements Runnable {

	// Constants
	public static final String SERVER_ADDRESS = "localhost";
	public static final int PORT = 2017;
	
	// Member variables
	protected ResultConsumer resultConsumer;
	protected Socket socket;
	protected ObjectInputStream oiStream;
	protected ObjectOutputStream ooStream;
	
	// Constructor
	public GameClient(ResultConsumer resultConsumer) {
		// Save a reference to the result consumer
		this.resultConsumer = resultConsumer;
		
		// Try connecting
		System.out.println("Connecting to game server...");
		boolean connectionSuccessful = false;
		while(!connectionSuccessful) {
			try {
				socket = new Socket(SERVER_ADDRESS,PORT);
				connectionSuccessful = true;
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(!connectionSuccessful) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Connection not successful, trying again...");
			}
		}
		System.out.println("Connected to game server!");
		
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
	}
	
	// Sends a player action to the client
	public void sendPlayerAction(PlayerAction playerAction) {
		try {
			ooStream.writeObject(playerAction);
			ooStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Listens for incoming game results and enqueues them
	@Override
	public void run() {
		while(true) {
			try {
				// Get the game result and add to game server queue
				GameResult newGameResult = (GameResult) oiStream.readObject();
				resultConsumer.acceptGameResult(null, newGameResult);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
