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

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.logic.CultivationGame;
import com.potatoes.cultivation.logic.GameManager;
//import com.potatoes.cultivation.logic.Command;
import com.potatoes.cultivation.logic.Player;

public class Client{
	
	String host;
	int port;
	private Socket socket;
	BlockingQueue<Protocol> incomingQueue = new LinkedBlockingQueue<>();
	List<ProtocolHandler> handlers = new LinkedList<>();
	
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
		
		// heartbeat thread (ie tells server the connection is alive)
		new Thread(new HeartBeat(this, game)).start();
		
		// event listener
		new Thread(new EventListener(this)).start();
		
		// socket listener
		new Thread(new SocketListener(this));
		
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
	
	void sendHeartbeat(){
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

//	public void updateRoomInfo(int number) {
//		try {
//			Method getPlayersForRoom = this.getClass().getDeclaredMethod("getPlayersForRoom", int.class);
//			taskQueue.add(new ClientTask(getPlayersForRoom, number));
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void updateRoomInfo(Collection<Player> players){
//		game.
//	}
	
	public Set<Player> getPlayersForRoom(int number) {
		try {
			System.out.println("Getting players for room "+number);
			out.writeObject(new GetARoomProtocol(number));
			GetARoomProtocol returnedMessage = (GetARoomProtocol) in.readObject();
			game.updateRoomPlayerList(returnedMessage.getList());
			return returnedMessage.getList();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null; 
	}
	
	public List<Player> updateLobby(){return new LinkedList<Player>();}
//	public void endTurn(List<Command> actions){}
	
	public void joinRoom(int number, Player p) {
		try {
			out.writeObject(new JoinRoomProtocol(number, p));
//			JoinRoomProtocol returnedMessage = (JoinRoomProtocol) in.readObject();
//			return returnedMessage.getResult();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		return false; 
	}
	
	public void login(String username, String password){
		try{
			out.writeObject(new LoginProtocol(username, password));
//			LoginProtocol returnedMessage = (LoginProtocol) in.readObject();
//			game.setPlayer(returnedMessage.player());
//			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e){
//			e.printStackTrace();
		}
//		return null;
	}
	
	public void register(String username, String password){
		try{
			out.writeObject(new RegisterProtocol(username, password));
//			RegisterProtocol returnedMessage = (RegisterProtocol) in.readObject();
//			game.setPlayer(returnedMessage.player());
//			return returnedMessage.player();
		} catch (IOException e) {
			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		}
//		return null;
	}
//	
//	class ClientThread implements Runnable {
//		private Client client;
//		
//		public ClientThread(Client master) {
//			client = master;
//		}
//
//		@Override
//		public void run() {
//			while(true) {
//				try {
//					ClientTask c = taskQueue.take();
//					Method toExecute = c.getMethodToExecute();
//					Object[] params = c.getParameters();
//					
//					Object methodReturn = toExecute.invoke(client, params);
//					System.out.println(methodReturn);
//					switch(toExecute.getName()) {
//						case "doLogin": 
//							game.setPlayer((Player) methodReturn);
//							break;
//						case "createAccount":
//							game.setPlayer((Player) methodReturn);
//							break;
//						case "getPlayersForRoom":
//							game.updateRoomPlayerList((Set<Player>) methodReturn);
//							break;
//					}
//				} catch (InterruptedException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}
