package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
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
	
	public Player login(String username, String password){
		try{
			Socket socket = new Socket(host, port);
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new LoginProtocol(username, password));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			LoginProtocol returnedMessage = (LoginProtocol) in.readObject();
			socket.close();
			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return Player.nullPlayer;
	}
	
	public Player createAccount(String username, String password){
		try{
			Socket socket = new Socket(host, port);
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new RegisterProtocol(username, password));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			RegisterProtocol returnedMessage = (RegisterProtocol) in.readObject();
			socket.close();
			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<Player> updateLobby(){return new LinkedList<Player>();}
	public void endTurn(List<Command> actions){}
}
