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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	Map<String, Socket> playerToSockets = new ConcurrentHashMap<>();
	Map<String, Long> lastSeen = new ConcurrentHashMap<>();
	
	public Server(int port) {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						final ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
						Protocol protocol = (Protocol) in.readObject();
						if(protocol instanceof LoginProtocol){
							protocol.execute(server);
							String username = ((LoginProtocol) protocol).player().getUsername();
							playerToSockets.put(username, incoming);
							lastSeen.put(username, System.currentTimeMillis());
							new ObjectOutputStream(incoming.getOutputStream()).writeObject(protocol);
							// Thread to listen on the socket
							new Thread(new Runnable() {
								@Override
								public void run() {
									while(!incoming.isClosed()){
										try {
											Protocol protocol = (Protocol) in.readObject();
											queue.put(new ServerTask(incoming, protocol));
											Thread.sleep(100);
										} catch (ClassNotFoundException| IOException | InterruptedException e) {
											e.printStackTrace();
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
					catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
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
						System.out.println("New task was taken");
						task.protocol.execute(server);
						new ObjectOutputStream(task.out.getOutputStream()).writeObject(task.protocol);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		// Thread to monitor clients
		new Thread(new Runnable() {
			@Override
			public void run() {
				Iterator<Entry<String, Long>> it = lastSeen.entrySet().iterator();
				while (it.hasNext()){
					Entry<String, Long> entry = it.next();
					long current = System.currentTimeMillis();
					if(current - entry.getValue() > 60000){
						// if last seen was more than a minute ago
						String user = entry.getKey();
						try {
							playerToSockets.get(user).close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						it.remove();
						System.out.println(user + " has disconnected");
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		Server server = new Server(7470);
		new Thread(server).start();
	}
	
	////////////////////Functions for server//////////////////////////
	
	void heartbeat(String username){
		if(this.lastSeen.get(username) != null){
			this.lastSeen.put(username, System.currentTimeMillis());
		}
	}
	
	Map<String, Boolean> areOnline(List<String> usernames){
		Map<String, Boolean> online = new HashMap<String, Boolean>();
		for (String username : usernames) {
			online.put(username, this.lastSeen.get(username)!=null && !this.playerToSockets.get(usernames).isClosed());
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
				this.putIfAbsent(this.winsAndLoses, elements[0], new WinsAndLosses(Integer.getInteger(elements[2]).intValue(), Integer.getInteger(elements[3]).intValue()));
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
}
