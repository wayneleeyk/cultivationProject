package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
	
	
	ServerSocket socket;
	Queue<Protocol> queue = new LinkedList<Protocol>();
	public Server(int port) {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		while(true){
			try {
				Socket incoming = socket.accept();
				ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
				Protocol protocol = (Protocol) in.readObject();
				this.queue.add(protocol);
				ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream());
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void login(String name, String password){
		System.out.println(name + " " + password);
	}
	
}
