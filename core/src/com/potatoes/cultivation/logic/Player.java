package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.Arrays;

import com.potatoes.cultivation.Cultivation;
import com.potatoes.cultivation.networking.Client.WinLoss;

public class Player implements Serializable{
	
	private static final long serialVersionUID = 6376954568789670281L;
	public static final Player nullPlayer = new Player("");
	
	String username = null;

	public Player(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "Username : "+username;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public boolean notNull(){
		return this.username!=null && !this.username.equals("");
	}
	
	public void increaseWins(){
		Cultivation.CLIENT.addWinLoss(WinLoss.WIN);
	}
	
	public void increaseLosses(){
		Cultivation.CLIENT.addWinLoss(WinLoss.LOSS);
	}
	public PlayerStatus getStatus() {
		return (Cultivation.CLIENT.areOnline(Arrays.asList(new String[]{this.username})).get(this.username)) ? PlayerStatus.Online : PlayerStatus.Offline;
	}
}
