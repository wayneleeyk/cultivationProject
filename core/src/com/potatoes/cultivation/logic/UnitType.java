package com.potatoes.cultivation.logic;
public enum UnitType {
	Peasant, Infantry, Soldier, Knight;
	public int getCostFrom(UnitType type){
		return (type.ordinal() - this.ordinal()) * 10;
	}	
	public boolean canInvadeBy(UnitType type){
		return type.ordinal() > this.ordinal();
	}	
	public int getSalary(){
		return 2*(int) Math.pow(3, this.ordinal());
	}
}

