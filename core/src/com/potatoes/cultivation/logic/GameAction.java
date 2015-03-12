package com.potatoes.cultivation.logic;

import java.io.Serializable;

public interface GameAction extends Serializable {
	public void execute(CultivationGame game);
	
	class HireVillagerAction implements GameAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4271092470984585609L;
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
		/**
		 * 
		 */
		private static final long serialVersionUID = -4038577797417726543L;
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
		/**
		 * 
		 */
		private static final long serialVersionUID = -3332608300692384612L;
		Village v;
		Tile t;
		
		public UpgradeVillageAction(Village v) {
			this.v = v;
			t = v.getTile();
		}
		
		@Override
		public void execute(CultivationGame game) {
			Village toUpgrade = game.getGameMap().getMap()[t.x][t.y].getVillage();
			game.upgradeVillage(toUpgrade);
		}
		
	}
}
