package cultivationSkeleton;

public class Village implements Comparable<Village> {

	private VillageType myType;
	private int gold;
	private int wood;
	private Region myRegion;
	private Tile myTile;
	private Player owner;
	
	public VillageType getType(){
		return myType;
	}
	
	public int getGold(){
		return gold;
	}
	
	public int getWood(){
		return wood;
	}
	
	public Region getRegion(){
		return myRegion;
	}
	
	public Tile getTile(){
		return myTile;
	}
	
	public Player getOwner(){
		return owner;
	}
	
	public void setType(VillageType type){
		
	}
	
	public void addGold(int gold){
		
	}
	
	public void removeGold(int gold){
		
	}
	
	public void addWood(int wood){
		
	}
	
	public void removeWood(int wood){
		
	}
	
	@Override 
	public int compareTo(Village v){
		return 0;
	}
}
