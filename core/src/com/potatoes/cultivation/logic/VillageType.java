package com.potatoes.cultivation.logic;

public enum VillageType {

	Hovel(1), Town(2), Fort(5), Castle(10);
	private int maxHP;
	private VillageType(int maxHP) {
		this.maxHP = maxHP;
	}
	public int maxHP(){
		return this.maxHP;
	}

	// if the type is a Hovel, no cost. Otherwise it costs 8 wood, though a fort also needs a Town to upgrade.
	public int getCost(){
		return (this.equals(Hovel)) ? 0 : (this.equals(Castle))? 12 : 8;
	}

	//returns true if "this" village type is a greater rank that the type passed to the function
	public boolean isHigherRank(VillageType type){
		return  this.ordinal() - type.ordinal() > 0;
	}

}
