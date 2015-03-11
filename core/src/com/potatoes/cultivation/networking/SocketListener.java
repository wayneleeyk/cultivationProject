package com.potatoes.cultivation.networking;

import java.io.IOException;

final class SocketListener implements Runnable {
	/**
	 * 
	 */
	private final Client client;

	/**
	 * @param client
	 */
	SocketListener(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		while(true){
			try {
				Protocol protocol = (Protocol) this.client.in.readObject();
				this.client.incomingQueue.offer(protocol);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}