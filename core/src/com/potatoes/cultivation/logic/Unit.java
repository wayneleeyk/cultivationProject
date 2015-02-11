package cultivationSkeleton;

public class Unit {

	private UnitType mytype;
	private ActionType currentAction;
	private Tile myTile;
	private Village myVillage;
	
	public ActionType getCurrentAction(){
		return currentAction;
	}
	
	public UnitType getType(){
		return mytype;
	}
	
	public Tile getTile(){
		return myTile;
	}
	
	public Village getVillage(){
		return myVillage;
		
	}
	
	public void updateAction (ActionType action){
		
	}
	
	public void updateType(UnitType type){
		
	}
	
	public boolean tryInvadeTile(Tile t){
		return false;
	}
	
	public void updateTileLocation(Tile t){
		
	}
}
