package com.potatoes.cultivation.logic;

import java.io.Serializable;

public interface GameAction extends Serializable {
	public void execute(CultivationGame game);
	
	class EndTurnAction implements GameAction {
		private static final long serialVersionUID = -5840823525714838533L;
		
		@Override
		public void execute(CultivationGame game) {
			game.increaseRoundCount();
			game.beginTurn(game.turnOf());
		}
	}
	
	class HireVillagerAction implements GameAction {
		private static final long serialVersionUID = 4271092470984585609L;
		Tile t;
		public HireVillagerAction(Tile t) {
			this.t = t;
		}
		
		@Override
		public void execute(CultivationGame game) {
			game.hireVillager(game.getGameMap().getMap()[t.x][t.y]);
			game.getWorld().createPotatoAt(t.x, t.y);
		}
	}
	
	class HireCannonAction implements GameAction {
		private static final long serialVersionUID = -8273993652190448494L;
		Tile t;
		public HireCannonAction(Tile t) {
			this.t = t;
		}
		@Override
		public void execute(CultivationGame game) {
			game.hireCannon(game.getGameMap().getMap()[t.x][t.y]);
		}
	}
	
	class MoveUnitAction implements GameAction {
		private static final long serialVersionUID = -4038577797417726543L;
		Tile unitTile;
		Tile target;
		
		public MoveUnitAction(Tile unitTile, Tile destTile) {
			this.unitTile = unitTile;
			target = destTile;
		}
		@Override
		public void execute(CultivationGame game) {
//			game.moveUnit(unit, target);
//			Tile unit = game.getGameMap().getMap()[unitTile.x][unitTile.y];
//			Tile dest = game.getGameMap().getMap()[target.x][target.y];
//			
//			if(unit == null || dest == null || unit.getUnit() == null) return;
//			
//			unit.getUnit().myTile = dest;
//			dest.setUnit(unit.getUnit());
//			
//			if(dest.getLandType() == LandType.Tree) {
//				dest.updateLandType(LandType.Grass);
//				unit.occupant.getVillage().addWood(1);
//			}
//			
//			// Graphics part
//			TileGroup[][] tgmap = game.getGameMap().getTGMap();
//			tgmap[dest.x][dest.y].setUnit(tgmap[unit.x][unit.y].getUnit());
//			tgmap[dest.x][dest.y].getTileImage().setColor(new Color(1, 0, 0, 0.99f));
		}
	}
	
	class FireCannonAction implements GameAction {
		private static final long serialVersionUID = -5958942837914004532L;
		Tile target;
		public FireCannonAction(Tile target) {
			this.target = target;
		}
		public void execute(CultivationGame game) {
			game.fireAt(game.getGameMap().getMap()[target.x][target.y]);
		}
	}
	

	class UpgradeVillageAction implements GameAction {
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
	
	class UpgradePotatoAction implements GameAction {
		private static final long serialVersionUID = -2366301428781363053L;
		Tile t;
		Unit u;
		UnitType uType;
		
		public UpgradePotatoAction(Unit u, UnitType uType) {
			this.u = u;
			this.t = u.getTile();
			this.uType = uType;
		}
		
		@Override
		public void execute(CultivationGame game) {
			Unit toUpgrade = game.getGameMap().getMap()[t.x][t.y].getUnit();
			if(game.upgradeUnit(toUpgrade, uType)) {
				game.getWorld().upgradePotatoAt(t.x, t.y, uType);
			}
		}
		
	}
	
}
