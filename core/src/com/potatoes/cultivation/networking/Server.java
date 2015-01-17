package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable{
	
	
	ServerSocket socket;
	BlockingQueue<ServerTask> queue = new LinkedBlockingQueue<ServerTask>();
	public Server(int port) {
		try {
			this.socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						Socket incoming = socket.accept();
						System.out.println("Accepted a connection");
						ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
						Protocol protocol = (Protocol) in.readObject();
						queue.put(new ServerTask(incoming, protocol));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						ServerTask task = queue.take();
						System.out.println("New task was taken");
						task.protocol.execute();
						new ObjectOutputStream(task.out.getOutputStream()).writeObject(task.protocol);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	static boolean login(String name, String password){
		return name.equals("hey") && password.equals("potatoes");
	}
	
}
