package com.potatoes.cultivation.logic;
public enum UnitType {
	Peasant(10), Infantry(20), Soldier(30), Knight(40), Cannon(35);
	private int cost;
	private UnitType(int cost) {
		this.cost = cost;
	}

	public int getCost(){
		return this.cost;
	}
	
	/**
	 * @param type		Type to evolve from
	 * @return			The cost in gold nuggets
	 */
	public int getCostFrom(UnitType type){
		return this.cost  - type.cost;
	}	
	
	public boolean canInvadeBy(UnitType type){
		if(type.equals(UnitType.Knight)) return true;
		return type.ordinal() - this.ordinal() > 0;
	}	
	
	public boolean canGoNear(UnitType type){
		if(type.equals(UnitType.Cannon)) return true;
		return type.ordinal() - this.ordinal() > 0;
	}
	
	public int getSalary(){
		if(this.equals(UnitType.Cannon)) return 5;
		return 2 * (int)Math.pow(3, this.ordinal()) ;
	}
}

