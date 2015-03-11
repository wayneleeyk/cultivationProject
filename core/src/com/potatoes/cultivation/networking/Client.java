package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Set;

import jdk.nashorn.internal.ir.Block;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameManager;
//import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client{
	
	String host;
	int port;
	private Socket socket;
	private BlockingQueue<ClientTask> taskQueue = new LinkedBlockingQueue<ClientTask>();
	private BlockingQueue<Protocol> incomingQueue = new LinkedBlockingQueue<>();
	
	private List<ProtocolHandler> handlers = new LinkedList<>();
	
	Cultivation game;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public Client(final Cultivation game, String host, int port) {
		this.host = host;
		this.port = port;
		this.game = game;
		try {
			this.socket = new Socket(host, port);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			out.flush();
			this.in = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new ClientThread(this)).start();
		
		// heartbeat thread (ie tells server the connection is alive)
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting heart ");
				while(true){
					if(game.player!=null) sendHeartbeat();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		// event listener
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Protocol p = incomingQueue.take();
						for (ProtocolHandler protocolHandler : handlers) {
							protocolHandler.handle(p);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public void insertHandler(ProtocolHandler handler){
		this.handlers.add(handler);
	}
	
	public void removeHandler(ProtocolHandler handler){
		this.handlers.remove(handler);
	}
	
	public void clearAllHandlers(){
		this.handlers.clear();
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
	
	public void startGame(CultivationGame game){
		try {
			out.writeObject(new GameDataProtocol(this.game.player, game));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<Player, Boolean> areOnline(List<Player> usernames){
		try {
			out.writeObject(new GetPlayersStatusProtocol(usernames));
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
			out.writeObject(new HeartbeatProtocol(this.game.player.getUsername()));
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
			out.writeObject((status.equals(WinLoss.WIN))? new AddWinProtocol(playerName):new AddLossProtocol(playerName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getWinLoss(Player player, WinLoss status){
		String playerName = player.getUsername();
		try {
			out.writeObject(new GetWinLossProtocol(playerName));
			GetWinLossProtocol returnedMessage = (GetWinLossProtocol) in.readObject();
//			socket.close();
			return (status.equals(WinLoss.WIN))? returnedMessage.getWins() : returnedMessage.getLosses();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException(playerName + " is not a valid player found in server");
	}

	public void updateRoomInfo(int number) {
		try {
			Method getPlayersForRoom = this.getClass().getDeclaredMethod("getPlayersForRoom", int.class);
			taskQueue.add(new ClientTask(getPlayersForRoom, number));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
//	public void updateRoomInfo(Collection<Player> players){
//		game.
//	}
	
	public Set<Player> getPlayersForRoom(int number) {
		try {
			System.out.println("Getting players for room "+number);
			out.writeObject(new GetARoomProtocol(number));
			GetARoomProtocol returnedMessage = (GetARoomProtocol) in.readObject();
			return returnedMessage.getList();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	public List<Player> updateLobby(){return new LinkedList<Player>();}
//	public void endTurn(List<Command> actions){}
	
	public boolean joinRoom(int number, Player p) {
		try {
			out.writeObject(new JoinRoomProtocol(number, p));
			JoinRoomProtocol returnedMessage = (JoinRoomProtocol) in.readObject();
			return returnedMessage.getResult();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false; 
	}
	
	public Player doLogin(String username, String password){
		try{
			out.writeObject(new LoginProtocol(username, password));
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
			out.writeObject(new RegisterProtocol(username, password));
			RegisterProtocol returnedMessage = (RegisterProtocol) in.readObject();
			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
						case "getPlayersForRoom":
							game.updateRoomPlayerList((Set<Player>) methodReturn);
							break;
					}
				} catch (InterruptedException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
