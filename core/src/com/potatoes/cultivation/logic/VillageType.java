package com.potatoes.cultivation.logic;

public enum VillageType {

	Hovel, Town, Fort;
	
	
	// if the type is a Hovel, no cost. Otherwise it costs 8 wood, though a fort also needs a Town to upgrade.
	//If this method fails for some reason, return -1.
	public int getCost(){
		if(this.ordinal() == 0){
			return 0;
		}
		else if(this.ordinal() == 1 || this.ordinal() == 2){
			return 8;
		}
		else{
		return -1;
		}
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
