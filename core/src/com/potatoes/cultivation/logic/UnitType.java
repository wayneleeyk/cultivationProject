package com.potatoes.cultivation.logic;

public enum UnitType {
	
	Peasant, Infantry, Soldier, Knight;

	/*Returns the cost of the UnitType
		Peasant = 10g
		Infantry = 20g
		Soldier = 30g
		Knight = 40g
	  returns -1 on fail
	*/
	public int getCostFrom(UnitType type){
		if (this.ordinal() == 0){
			return 10;
		}
		else if(this.ordinal() == 1){
			return 20;
		}
		else if(this.ordinal() == 2){
			return 30;
		}
		else if(this.ordinal() == 3){
			return 40;
		}
		else{
		return -1;
		}
	}	
	
	//I'm going to assume this method is being called on an enemy tile.... not finished yet
	public boolean canInvadeBy(UnitType type){
		if (type.ordinal() == 0 ){
			return false;
		}
		
		return false;
	}
	
	
	/* Returns the salary of the unit Type.
	 * Peasant =  2g
	 * Infantry = 6g
	 * Soldier =  18g
	 * Knight =   54g
	 * Returns -1 upon some sort of failure.
	 */
	public int getSalary(UnitType type){
		if (this.ordinal() == 0){
			return 2;
		}
		else if(this.ordinal() == 1){
			return 6;
		}
		else if(this.ordinal() == 2){
			return 18;
		}
		else if(this.ordinal() == 3){
			return 54;
		}
		else{
		return -1;
		}
	}
	
	
	
	
	
	
	
}

