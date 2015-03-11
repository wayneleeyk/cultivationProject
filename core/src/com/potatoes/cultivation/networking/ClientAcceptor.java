package com.potatoes.cultivation.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

final class ClientAcceptor implements Runnable {
	/**
	 * 
	 */
	private final Server server;

	ClientAcceptor(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		while(true){
			try {
				final Socket incoming = server.socket.accept();
				System.out.println("Accepted a connection");
				final ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream());
				out.flush();
				final ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
				Protocol protocol = (Protocol) in.readObject();
				
				if(protocol instanceof LoginProtocol || protocol instanceof RegisterProtocol){
					protocol.execute(server);
					String user = "";
					if(protocol instanceof LoginProtocol) {
						user = ((LoginProtocol) protocol).player().getUsername();
					}
					else {
						user = ((RegisterProtocol) protocol).player().getUsername();
					}
					final String username = user;
					server.usernameToUser.put(username, new User(incoming, out));
					
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
									ClientAcceptor.this.server.queue.put(new ServerTask(out, protocol));
									Thread.sleep(100);
								} 
								catch (ClassNotFoundException| IOException | InterruptedException e) {
									try {
										ClientAcceptor.this.server.usernameToUser.get(username).closeConnection();
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
				e.printStackTrace();
			}
		}
	}
}