package com.potatoes.cultivation.networking;

import java.net.Socket;

public class ServerTask {
	
	Socket out; 
	Protocol protocol;
	public ServerTask(Socket out, Protocol protocol) {
		this.out = out;
		this.protocol = protocol;
	}
}
