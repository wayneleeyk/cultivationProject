package com.potatoes.cultivation.logic;

import java.net.Socket;
import java.util.Map;

public class ClientConnectionManager {

	private Map<Player,Socket> playerSocket;
	

	// already have this done. Returns the socket based on the given player
public Socket getSocket(Player p){
	Socket socket = playerSocket.get(p);
	return socket;
}


	
	
}
