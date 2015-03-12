package com.potatoes.cultivation.logic;
//Author: Shivani Sharma 
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
	
	/* I'm assuming this method will be called on an enemy tile.
	 * Peasants ->can't invade anything, those poor things.
	 * Infantry -> can only invade a tile that has no unit or has a peasant.
	 * soldier-> can invade if infantry,peasant, or no unit
	 * Knight-> you better watch out cuz ain't nothing stopping this unit from killing you (unless you're a knight too)
	 */
	public boolean canInvadeBy(UnitType type){
		//if the unit passed has a greater value than the unit the method is called on, return true.
		if (type.ordinal() - this.ordinal() > 0 ){
		    return true;
		}
		else {
			return false;
		}
	}	
	
	
	/* Returns the salary of the unit Type.
	 * Peasant =  2g
	 * Infantry = 6g
	 * Soldier =  18g
	 * Knight =   54g
	 * Returns -1 upon some sort of failure.
	 */
	public int getSalary(){
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

