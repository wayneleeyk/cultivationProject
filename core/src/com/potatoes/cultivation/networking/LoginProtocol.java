package com.potatoes.cultivation.networking;


public class LoginProtocol implements Protocol{
	private static final long serialVersionUID = 4688377699824719114L;
	private String name, password;
	private boolean successful = false;
	public LoginProtocol(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	@Override
	public void execute() {
		this.setSuccessful(Server.login(name, password));
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
