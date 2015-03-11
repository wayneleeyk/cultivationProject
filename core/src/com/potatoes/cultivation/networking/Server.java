package com.potatoes.cultivation.networking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.potatoes.cultivation.logic.Player;

public class Server implements Runnable{

	final static File accounts = tryRead("./Accounts/accounts.dat");
	
	ServerSocket socket;
	BlockingQueue<ServerTask> queue = new LinkedBlockingQueue<ServerTask>();
	Map<String, String> accountsForLogin = new ConcurrentHashMap<>();
	Map<String, WinsAndLosses> winsAndLoses = new ConcurrentHashMap<>();
	
	Map<String, User> usernameToUser = new ConcurrentHashMap<String, User>();
	
	List<Set<Player>> gameRooms = Collections.synchronizedList(new ArrayList<Set<Player>>(10));
	
	public Server(int port) {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initialize game room 0 for testing purposes
		gameRooms.add(0,new LinkedHashSet<Player>());
	}

	@Override
	public void run(){
		final Server server = this;
		// Thread to accept new connections
		// Initial protocol must be a LoginProtocol 
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						final Socket incoming = socket.accept();
						System.out.println("Accepted a connection");
						final ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream());
						out.flush();
						final ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
						Protocol protocol = (Protocol) in.readObject();
						
						if(protocol instanceof LoginProtocol){
							protocol.execute(server);
							final String username = ((LoginProtocol) protocol).player().getUsername();
							usernameToUser.put(username, new User(incoming, out));
							
							out.writeObject(protocol);
							// Thread to listen on the socket
							System.out.println("Starting task listener...");
							new Thread(new Runnable() {
								@Override
								public void run() {
									System.out.println("Task Listener started!");
									while(!incoming.isClosed()){
										try {
											Protocol protocol = (Protocol) in.readObject();
											queue.put(new ServerTask(incoming, out, protocol));
											Thread.sleep(100);
										} 
										catch (ClassNotFoundException| IOException | InterruptedException e) {
											try {
												usernameToUser.get(username).closeConnection();
											} catch (IOException io) {
												io.printStackTrace();
											}
											return;
										}
									}
								}
							}).start();
						}
						else{
							in.close();
							incoming.close();
						}
					}
					catch (IOException |ClassNotFoundException e) {
					}
				}
			}
		}).start();
		// Thread to process tasks
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						ServerTask task = queue.take();
						task.protocol.execute(server);
						task.oos.writeObject(task.protocol);
						task.oos.flush();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		// Thread to monitor clients
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while(true){
//					Iterator<Entry<String, User>> it = usernameToUser.entrySet().iterator();
//					while (it.hasNext()){
//						Entry<String, User> entry = it.next();
//						long current = System.currentTimeMillis();
////						System.out.println(entry.getKey() +" was last seen "+(current - entry.getValue().lastSeen));
//						if(current - entry.getValue().lastSeen > 3000){
//							// if last seen was more than half a minute ago
//							String user = entry.getKey();
//							try {
//								entry.getValue().closeConnection();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//							it.remove();
//							System.out.println(user + " has disconnected");
//						}
//					}
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
	}
	
	public static void main(String[] args) {
		Server server = new Server(7470);
		new Thread(server).start();
	}
	
	////////////////////Functions for server//////////////////////////
	
	void propagate(Player sender, ActionBlockProtocol actionBlock){
		for (Player p : GamesManager.opponentsOf(sender)) {
			User user = usernameToUser.get(p.getUsername());
			queue.add(new ServerTask(user.socket, user.oos ,actionBlock));
		}
	}
	
	void propagate(Player sender, GameDataProtocol game){
		for(Player p: GamesManager.opponentsOf(sender)){
			User user = usernameToUser.get(p.getUsername());
			queue.add(new ServerTask(user.socket, user.oos,game));
		}
	}
	
	void heartbeat(String username){
		User user = usernameToUser.get(username);
		if(user != null){
			user.updateLastSeen();
		}
	}
	
	Map<Player, Boolean> areOnline(List<Player> players){
		Map<Player, Boolean> online = new HashMap<>();
		for (Player player : players) {
			User user = usernameToUser.get(player.getUsername());
			online.put(player, user!=null && !user.socket.isClosed());
		}
		return online;
	}
	
	Player login(String username, String password){
		System.out.println("Login with username:"+username+" password:"+password);
		this.readAccounts(accounts);
		if( !password.equals("") && this.getOrDefault(username).equals(password)){
			return new Player(username);
		}
		return Player.nullPlayer;
	}

	Player register(String username, String password) {
		System.out.println("Registering with username:"+username+" password:"+password);
		this.readAccounts(accounts);
		if( !this.accountsForLogin.containsKey(username) ){
			System.out.println("Account not yet existing, creating new account");
			try (PrintWriter writer = new PrintWriter(new FileOutputStream(accounts, true))){
				writer.append(username+"\t"+password+"\t"+0+"\t"+0+"\n");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			readAccounts(accounts);
			return new Player(username);
		}
		return Player.nullPlayer;
	}

	private void readAccounts(File accounts) {
		try(InputStream in = Files.newInputStream(accounts.toPath())){
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = br.readLine()) != null){
				String[] elements = line.split("\t");
				this.putIfAbsent(this.accountsForLogin, elements[0], elements[1]);
				this.putIfAbsent(this.winsAndLoses, 
								elements[0], 
								new WinsAndLosses(Integer.valueOf(elements[2]).intValue(), Integer.valueOf(elements[3]).intValue()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	<K,V> void putIfAbsent(Map<K, V> map, K key, V value){
		if( map.get(key) == null){
			map.put(key, value);
		}
	}
	
	String getOrDefault(String key){
		String value = this.accountsForLogin.get(key) ;
		return (value != null) ? value : "";
	}
	
	static private File tryRead(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	// Class for wins and losses, along with functions related to it
	class WinsAndLosses{
		int wins, losses;
		public WinsAndLosses() {
			this.wins = 0;
			this.losses = 0;
		}
		public WinsAndLosses(int wins, int losses){
			this.wins = wins;
			this.losses = losses;
		}
	}
	
	public void addWin(String playerName){
		WinsAndLosses value = this.winsAndLoses.get(playerName);
		if(value != null){
			value.wins += 1;
		}
		else{
			throw new IllegalArgumentException("Wrong player name");
		}
	}
	
	
	public void addLoss(String playerName){
		WinsAndLosses value = this.winsAndLoses.get(playerName);
		if(value != null){
			value.losses += 1;
		}
		else{
			throw new IllegalArgumentException("Wrong player name");
		}
	}
	
	public int getWins(String playerName){
		WinsAndLosses value = this.winsAndLoses.get(playerName);
		if(value != null){
			return value.wins;
		}
		else{
			throw new IllegalArgumentException("Wrong player name");
		}
	}
	
	public int getLosses(String playerName){
		WinsAndLosses value = this.winsAndLoses.get(playerName);
		if(value != null){
			return value.losses;
		}
		else{
			throw new IllegalArgumentException("Wrong player name");
		}
	}
	
	public Set<Player> getPlayersForRoom(int i) {
//		if(gameRooms.size() - 1 < i) return null;
//		if(gameRooms.get(i) == null) {
//			gameRooms.set(i, new HashSet<Player>());
//		}
//		
//		return gameRooms.get(i);
		
		return new HashSet<>( gameRooms.get(i) );
	}
	
	public boolean addPlayerToRoom(int i, Player p) {
//		if(gameRooms.size() - 1 < i) return false;
//		if(gameRooms.get(i) == null) return false;
//		else {
//			gameRooms.get(i).add(p);
//			return true;
//		}
		if(gameRooms.get(i) == null) return false;
		else {
			gameRooms.get(i).add(p);
			return true;
		}
	}
}
