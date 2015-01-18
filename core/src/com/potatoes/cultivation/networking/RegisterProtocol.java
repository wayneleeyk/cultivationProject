package com.potatoes.cultivation.networking;

public class RegisterProtocol implements Protocol {
	private static final long serialVersionUID = -8658082689064145170L;
	private String username, password;
	private boolean successful = false;
	
	public RegisterProtocol(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void execute() {
		setSuccessful(Server.register(username, password));
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

}
