package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.net.SocketException;

final class TaskProcessor implements Runnable {
	private final Server server;

	TaskProcessor(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		while(true){
			try {
				ServerTask task = server.queue.take();
				task.protocol.execute(server);
//				System.out.println("Writing object to client");
				task.oos.writeObject(task.protocol);
//				System.out.println("Wrote object to client " + task.protocol);
				task.oos.flush();
			} 
			catch (SocketException e){
				System.out.println("Socket has been closed");
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}