package com.potatoes.cultivation.logic;

public enum VillageType {

	Hovel, Town, Fort;
	
	
	
	public int getCost(){
		return 0;
	}
	//returns true if "this" village type is a greater rank that the type passed to the function
	public boolean isHigherRank(VillageType type){
		int rank = this.ordinal() - type.ordinal();
		if (rank > 0){
			return true;
		}
		else{
		return false;
		}
		
	}
	
	
}
