package com.potatoes.cultivation.logic;

import java.io.Serializable;

public interface GameAction extends Serializable {
	public void execute(CultivationGame game);
	class HireVillagerAction implements GameAction {
		private static final long serialVersionUID = 4271092470984585609L;
		Village village;
		
		public HireVillagerAction(Village v) {
			village = v;
		}
		
		@Override
		public void execute(CultivationGame game) {
			game.hireVillager(village);
			game.updateHUD(village);
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
			game.updateHUD(target);
		}
	}
	
	class UpgradeVillageAction implements GameAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3905309607177943084L;
		Village v;
		
		public UpgradeVillageAction(Village v) {
			this.v = v;
		}
		
		@Override
		public void execute(CultivationGame game) {
			game.upgradeVillage(v);
			game.updateHUD(v);
		}
		
		public Village getVillage() {
			return v;
		}
		
	}
}
