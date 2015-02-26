package com.potatoes.cultivation.logic;

public enum UnitType {
	
	Peasant, Infantry, Soldier, Knight;

	
	public int getCostFrom(UnitType type){
		return 0;
	}
	
	public boolean canInvadeBy(UnitType type){
		return false;
	}
	
	public int getSalary(UnitType type){
		return 0;
	}
	
	
	
	
	
	
	
}

