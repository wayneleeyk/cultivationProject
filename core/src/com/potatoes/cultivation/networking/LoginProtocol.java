package com.potatoes.cultivation.networking;

public class LoginProtocol implements Protocol{
	private String name, password;
	public LoginProtocol(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	@Override
	public void execute() {
		Server.login(name, password);
	}
}
