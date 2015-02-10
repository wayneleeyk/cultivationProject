package cultivationSkeleton;

public enum VillageType {

	Hovel, Town, Fort;
	
	
	
	public int getCost(){
		return 0;
	}
	
	public boolean isHigherRank(VillageType type){
		return false;
	}
	
	
}
