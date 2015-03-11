package com.potatoes.cultivation.networking;

public abstract class ProtocolHandler <T> {
	private T result = null;
	public abstract void handle(Protocol p);
	public T getResult(){
		while(result == null){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
