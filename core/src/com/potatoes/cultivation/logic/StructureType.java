package com.potatoes.cultivation.logic;


public enum StructureType{
	
	None, Tombstone, Watchtower, Road;
	
	public int getCost() {
		if (this.equals(StructureType.Watchtower)) {
			return 5;
		} else {
			return 0;
		}
	}
}
