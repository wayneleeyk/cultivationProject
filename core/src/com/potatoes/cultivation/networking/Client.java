package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client {
	
	private Socket socket ;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public Client(String host, int port) {
		this.socket = null;
		try {
			this.socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void login(String username, String password){
		try {
			this.out.writeObject(new LoginProtocol(username, password));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createAccount(String username, String password){}
	public List<Player> updateLobby(){return new LinkedList<Player>();}
	public void endTurn(List<Command> actions){}
	
	public static void main(String[] args) {
		Server s = new Server(20000);
		s.run();
		Client c = new Client("localhost", 20000);
		
	}
}
