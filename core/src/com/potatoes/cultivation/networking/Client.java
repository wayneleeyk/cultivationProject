package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.potatoes.cultivation.Cultivation;
//import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client{
	
	String host;
	int port;
	private Socket socket;
	private BlockingQueue<ClientTask> taskQueue = new LinkedBlockingQueue<ClientTask>();
	Cultivation game;
	
	
	public Client(Cultivation game, String host, int port) {
		this.host = host;
		this.port = port;
		this.game = game;
		try {
			this.socket = new Socket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new ClientThread(this)).start();
		// heartbeat thread (ie tells server the connection is alive)
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while(true){
//					sendHeartbeat();
//					try {
//						Thread.sleep(10000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
	}
	
	// Adds the login method to the queue for ClientThread to execute it
	public void login(String username, String password) {
			try {
				Method doLogin = this.getClass().getDeclaredMethod("doLogin", String.class, String.class);
				doLogin.setAccessible(true);
				taskQueue.add(new ClientTask(doLogin, username, password));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
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
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
//	public void addWinLoss(WinLoss status){
//		try {
//			Method add = this.getClass().getDeclaredMethod("doAddWinLoss", WinLoss.class);
//			add.setAccessible(true);
//			taskQueue.add(new ClientTask(add, status));
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void getWinLoss(WinLoss status){
//		try {
//			Method get = this.getClass().getDeclaredMethod("doGetWinLoss", WinLoss.class);
//			get.setAccessible(true);
//			taskQueue.add(new ClientTask(get, status));
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//	}
	
	
	public Map<Player, Boolean> areOnline(List<Player> usernames){
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new GetPlayersStatusProtocol(usernames));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			GetPlayersStatusProtocol status = (GetPlayersStatusProtocol) in.readObject();
			return status.areOnline();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}
	
	private void sendHeartbeat(){
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new HeartbeatProtocol(this.game.player.getUsername()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public enum WinLoss{
		WIN, LOSS;
	}
	
	public void addWinLoss(WinLoss status){
		String playerName = this.game.player.getUsername();
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject((status.equals(WinLoss.WIN))? new AddWinProtocol(playerName):new AddLossProtocol(playerName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getWinLoss(Player player, WinLoss status){
		String playerName = player.getUsername();
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new GetWinLossProtocol(playerName));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			GetWinLossProtocol returnedMessage = (GetWinLossProtocol) in.readObject();
			socket.close();
			return (status.equals(WinLoss.WIN))? returnedMessage.getWins() : returnedMessage.getLosses();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(playerName + " is not a valid player found in server");
	}

	public Player doLogin(String username, String password){
		try{
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new LoginProtocol(username, password));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			LoginProtocol returnedMessage = (LoginProtocol) in.readObject();
			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Player createAccount(String username, String password){
		try{
			new ObjectOutputStream(socket.getOutputStream()).writeObject(new RegisterProtocol(username, password));
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			RegisterProtocol returnedMessage = (RegisterProtocol) in.readObject();
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
