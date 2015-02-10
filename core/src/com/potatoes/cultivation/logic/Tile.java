package cultivationSkeleton;

public class Tile {

	private LandType myType;
	private StructureType structure;
	private Unit occupant;
	private Player player;
	
	
	public void addStructure(StructureType structure){
		
	}
	
	public StructureType getStructure(){
		return structure;
	}
	
	public void destroyStructure(){
		
	}
	
	public LandType getLandType(){
		return myType;
	}
	
	public void updateLandType(LandType land){
		
	}
	
	public void updateOwner(Player p){
		
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Unit getUnit(){
		return occupant;
	}
	
	public boolean canInvade(Unit U){
		return false;
	}
}
