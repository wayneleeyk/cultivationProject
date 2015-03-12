package com.potatoes.cultivation.logic;

public interface GameAction {
	public void execute(CultivationGame game);
	
	class HireVillagerAction implements GameAction {
		Village village;
		
		public HireVillagerAction(Village v) {
			village = v;
		}
		
		@Override
		public void execute(CultivationGame game) {
			game.hireVillager(village);
		}		
	}
	
	class MoveUnitAction implements GameAction {
		Unit unit;
		Tile target;
		
		public MoveUnitAction(Unit u, Tile t) {
			unit = u;
			target = t;
		}
		@Override
		public void execute(CultivationGame game) {
			game.moveUnit(unit, target);
		}
	}
	
	class UpgradeVillageAction implements GameAction {
		Village v;
		
		public UpgradeVillageAction(Village v) {
			this.v = v;
		}
		
		@Override
		public void execute(CultivationGame game) {
			game.upgradeVillage(v);
		}
		
	}
}
