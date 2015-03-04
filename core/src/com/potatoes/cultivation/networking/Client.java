package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;








import com.potatoes.cultivation.Cultivation;
//import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client{
	
	String host;
	int port;
	private Socket mySocket;
	private BlockingQueue<ClientTask> taskQueue = new LinkedBlockingQueue<ClientTask>();
	Cultivation game;
	
	public Client(Cultivation game, String host, int port) {
		this.host = host;
		this.port = port;
		this.game = game;
		new Thread(new ClientThread(this)).start();
	}
	
	// Adds the login method to the queue for ClientThread to execute it
	public void login(String username, String password) {
			try {
				Method doLogin = this.getClass().getDeclaredMethod("doLogin", String.class, String.class);
				doLogin.setAccessible(true);
				taskQueue.add(new ClientTask(doLogin, username, password));
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	// Adds the register method to the queue for ClientThread to execute it
	public void register(String username, String password) {
		try {
			Method register = this.getClass().getDeclaredMethod("createAccount", String.class, String.class);
			register.setAccessible(true);
			taskQueue.add(new ClientTask(register, username, password));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}

	private Player doLogin(String username, String password){
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
	
	private Player createAccount(String username, String password){
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
//	public void endTurn(List<Command> actions){}
	
	
	class ClientThread implements Runnable {
		private Client client;
		
		public ClientThread(Client master) {
			client = master;
		}

		@Override
		public void run() {
			while(true) {
				try {
					ClientTask c = taskQueue.take();
					Method toExecute = c.getMethodToExecute();
					Object[] params = c.getParameters();
					
					Object methodReturn = toExecute.invoke(client, params);
					System.out.println(methodReturn);
					switch(toExecute.getName()) {
						case "doLogin": 
							game.setPlayer((Player) methodReturn);
							break;
						case "createAccount":
							game.setPlayer((Player) methodReturn);
							break;
					}
				} catch (InterruptedException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
