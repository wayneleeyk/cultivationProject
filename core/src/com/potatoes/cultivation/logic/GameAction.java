package com.potatoes.cultivation.logic;

import java.io.Serializable;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.potatoes.cultivation.logic.GameMap.MapCoordinates;

public interface GameAction extends Serializable {
	public void execute(CultivationGame game);

	class EndTurnAction implements GameAction {
		private static final long serialVersionUID = -5840823525714838533L;

		@Override
		public void execute(CultivationGame game) {
			game.increaseRoundCount();
			game.beginTurn(game.turnOf());
			game.checkWinCondition();
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
		public void execute(final CultivationGame game) {
			final Unit unit = game.getGameMap().getMap()[unitTile.x][unitTile.y].occupant;
			final Tile targetTile = game.getGameMap().getMap()[target.x][target.y];

			// MoveV2, if this causes any unknown problem use V1
			if(targetTile.tryInvadeCheck(unit)) {
				Action moveUnit = new Action() {
					@Override
					public boolean act(float delta) {
						game.moveUnit(unit, targetTile);
						return true;
					}
				};
				game.getWorld().movePotatoV2(unitTile.x, unitTile.y, target.x, target.y, moveUnit);
			}

			// MoveV1
			//			if(game.moveUnit(unit, game.getGameMap().getMap()[target.x][target.y])){
			//				game.getWorld().movePotato(unitTile.x, unitTile.y, target.x, target.y);
			//			}
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

	class CultivateMeadowAction implements GameAction {
		private static final long serialVersionUID = -2011495395926540040L;
		Tile t;

		public CultivateMeadowAction(Tile unitTile) {
			t = unitTile;
		}

		@Override
		public void execute(CultivationGame game) {
			Unit cultivatingUnit = game.getGameMap().getMap()[t.x][t.y].getUnit();
			game.cultivateMeadow(cultivatingUnit);
		}
	}

	class BuildRoadAction implements GameAction {
		private static final long serialVersionUID = -2011495395926540040L;
		Tile t;

		public BuildRoadAction(Tile unitTile) {
			t = unitTile;
		}

		@Override
		public void execute(CultivationGame game) {
			Unit cultivatingUnit = game.getGameMap().getMap()[t.x][t.y].getUnit();
			game.buildRoad(cultivatingUnit);
		}
	}
	class BuildWatchtowerAction implements GameAction {
		private static final long serialVersionUID = -256171586814621743L;
		Tile t;

		public BuildWatchtowerAction(Tile tile) {
			t = tile;
		}

		@Override
		public void execute(CultivationGame game) {
			Tile realTile = game.getGameMap().getMap()[t.x][t.y];
			realTile.addStructure(StructureType.Watchtower);
		}
	}

	class GrowTreeAction implements GameAction {
		private static final long serialVersionUID = -5607805583574057082L;
		List<Tile> toGrow;

		public GrowTreeAction(List<Tile> treesToGrow) {
			toGrow = treesToGrow;
		}

		@Override
		public void execute(CultivationGame game) {
			game.growTrees(toGrow);
		}

	}

	class StoneVillagers implements GameAction {
		private static final long serialVersionUID = 5055189169666110678L;
		List<Tile> tiles;

		public StoneVillagers(List<Tile> t) {
			tiles = t;
		}

		@Override
		public void execute(CultivationGame game) {
			game.starveVillagers(tiles);

		}

	}

	class SpawnVillageAction implements GameAction{
		private static final long serialVersionUID = -3540404117233966292L;
		Player victim;
		List<Tile> regionTiles;
		Tile tile;
		public SpawnVillageAction(Player victim, List<Tile> regionTiles, Tile tile) {
			this.victim = victim;
			this.regionTiles = regionTiles;
			this.tile = tile;
		}
		@Override
		public void execute(CultivationGame game) {
			game.spawnVillage(this.victim, this.regionTiles, this.tile);
		}

	}

}
