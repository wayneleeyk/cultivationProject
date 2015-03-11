package com.potatoes.cultivation.networking;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerTask {
	
	Socket out; 
	ObjectOutputStream oos;
	Protocol protocol;
	public ServerTask(Socket out, ObjectOutputStream oos, Protocol protocol) {
		this.out = out;
		this.oos = oos;
		this.protocol = protocol;
	}
}
