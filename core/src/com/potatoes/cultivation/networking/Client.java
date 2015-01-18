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

public class Client{
	
	String host;
	int port;
	
	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public boolean login(String username, String password){
		try{
			Socket socket = new Socket(host, port);
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new LoginProtocol(username, password));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			LoginProtocol returnedMessage = (LoginProtocol) in.readObject();
			socket.close();
			return returnedMessage.isSuccessful();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void createAccount(String username, String password){}
	public List<Player> updateLobby(){return new LinkedList<Player>();}
	public void endTurn(List<Command> actions){}
	
	
	public static void main(String[] args) {
		Server s = new Server(20000);
		new Thread(s).start();
		Client c = new Client("localhost", 20000);
		System.out.println(c.login("hello", "world"));
		System.out.println(c.login("hey", "potatoes"));
	}
}
