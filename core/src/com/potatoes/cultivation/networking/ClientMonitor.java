package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

final class ClientMonitor implements Runnable {
		private final Server server;

		/**
		 * @param server
		 */
		ClientMonitor(Server server) {
			this.server = server;
		}

		@Override
		public void run() {
			while(true){
				Iterator<Entry<String, User>> it = this.server.usernameToUser.entrySet().iterator();
				while (it.hasNext()){
					Entry<String, User> entry = it.next();
					long current = System.currentTimeMillis();
//						System.out.println(entry.getKey() +" was last seen "+(current - entry.getValue().lastSeen));
					if(current - entry.getValue().lastSeen > 3000){
						// if last seen was more than half a minute ago
						String user = entry.getKey();
						try {
							entry.getValue().closeConnection();
						} catch (IOException e) {
							e.printStackTrace();
						}
						it.remove();
						System.out.println(user + " has disconnected");
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}