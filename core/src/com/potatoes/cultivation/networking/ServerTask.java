package com.potatoes.cultivation.networking;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerTask {
	
	ObjectOutputStream oos;
	Protocol protocol;
	public ServerTask(ObjectOutputStream oos, Protocol protocol) {
		this.oos = oos;
		this.protocol = protocol;
	}
}
