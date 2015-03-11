package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class User {
	Socket socket;
	ObjectOutputStream oos;
	Long lastSeen;
	public User(Socket socket, ObjectOutputStream oos) {
		this.socket = socket;
		this.oos = oos;
		this.lastSeen = System.currentTimeMillis();
	}
	
	public void closeConnection() throws IOException{
		this.oos.close();
		this.socket.close();
	}
	
	public long lastSeen(){
		return this.lastSeen;
	}
	
	void updateLastSeen(){
		this.lastSeen = System.currentTimeMillis();
	}
}
