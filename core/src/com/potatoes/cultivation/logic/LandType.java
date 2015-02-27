package com.potatoes.cultivation.logic;

//Author: Shivani Sharma

public enum LandType {

	Sea, Tree, Grass, Meadow;
	
	/* Sea or Tree Tile: No money awarded
	 * Grass (empty tile) : +1g
	 * Meadow: +2g
	 */
	public int getGoldValue(LandType type){
		if(type.ordinal() == 0 || type.ordinal() == 1){
			return 0;
		}
		else if(type.ordinal() == 2){
			return 1;
		}
		else if(type.ordinal() == 3){
			return 2;
		}
		else{
			return -1;
		}
	}
}
