package com.potatoes.cultivation.networking;

import com.potatoes.cultivation.Cultivation;

final class HeartBeat implements Runnable {
	private final Client client;
	private final Cultivation game;

	HeartBeat(Client client, Cultivation game) {
		this.client = client;
		this.game = game;
	}

	@Override
	public void run() {
		System.out.println("Starting heart ");
		while(true){
			if(game.player!=null) this.client.sendHeartbeat();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}