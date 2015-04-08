package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.potatoes.cultivation.helpers.ClickManager;
import com.potatoes.cultivation.helpers.ClickManager.ClickToCMListener;
import com.potatoes.cultivation.logic.ActionType;
import com.potatoes.cultivation.logic.Unit;
import com.potatoes.cultivation.logic.UnitType;

public class PotatoActor extends Actor {
		Unit gameUnit;
		String color;
		
		private float stateTime;
		private Animation potatoAnim;
		
		private ActorAssets myAssets;
		
		/**
		 * @param u - Unit
		 * @param a - ActorAssets
		 * @param c -  either "yellow", "purple" or "red", or defaults to "yellow"
		 * @param cm - Clickmanager
		 */
		public PotatoActor(Unit u, ActorAssets a, String c, final ClickManager cm) {
			gameUnit = u;
			myAssets = a;
			
			String type = u.myType.toString();
			color = c;
			if(!(color.equalsIgnoreCase("yellow") || color.equalsIgnoreCase("red") || color.equalsIgnoreCase("purple"))) {
				color = "yellow";
			}
			
			potatoAnim = myAssets.stringToAnimation.get("potato" + "_" + color + "_" + type.toLowerCase());
			int width = potatoAnim.getKeyFrame(0).getRegionWidth();
			int height = potatoAnim.getKeyFrame(0).getRegionHeight();
			this.setWidth(width);
			this.setHeight(height);
			this.setOriginX(width/2);
			
			// Add listener to forward to ClickManager
			this.addListener(cm.new ClickToCMListener());
			this.setName("PotatoActor");
		}
		
		@Override
		public void act(float delta) {
			stateTime += delta;
			super.act(delta);
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			if(!gameUnit.currentAction.equals(ActionType.ReadyForOrders)) {
				batch.setColor(0.4f,0.4f,0.4f,1);
			}
			TextureRegion currentFrame = potatoAnim.getKeyFrame(stateTime, true);
			batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
			batch.setColor(Color.WHITE);
			super.draw(batch, parentAlpha);
		}
		
		/**
		 * Directly set the animation to a.
		 * @param a
		 */
		public void upgradePotato(UnitType unitType) {
			potatoAnim = myAssets.stringToAnimation.get("potato_" + color + "_" + unitType.toString().toLowerCase());
			int width = potatoAnim.getKeyFrame(0).getRegionWidth();
			int height = potatoAnim.getKeyFrame(0).getRegionHeight();
			this.setWidth(width);
			this.setHeight(height);
			this.setOriginX(width/2);
		}
		
		
		/**** Not official ****/
		/*********************************************************/
		public void testMove() {
			MoveToAction to = new MoveToAction();
			
			// No checks yet (like whether the goto is left or right
			// just testing here
			ScaleToAction flip = new ScaleToAction();
			flip.setScale(-1, 1);
			this.addAction(flip);
			
			to.setDuration(5);
			to.setPosition(600, 100);
			to.setInterpolation(Interpolation.pow3Out);
			
			this.addAction(to);
		}
		
		public void moveToTile(final TileActor t, float duration) {
			MoveToAction to = new MoveToAction();
			
			ScaleToAction flip = new ScaleToAction();
			if(t.getX() > this.getParent().getX()) {
				flip.setScale(-1, 1);
			}
			else {
				flip.setScale(1, 1);
			}
			
			Vector2 oldPos = this.getParent().localToStageCoordinates(new Vector2(80, 30));
			Vector2 newPos = t.localToStageCoordinates(new Vector2(80, 30));
			
			Stage s = this.getParent().getStage();
			this.remove();
			s.addActor(this);
			this.toFront();
			this.setPosition(oldPos.x, oldPos.y);
			
			to.setDuration(duration);
			to.setPosition(newPos.x, newPos.y);
			to.setInterpolation(Interpolation.pow2Out);
			
			Action endMoveAction = new Action(){
				@Override
				public boolean act(float delta) {
					PotatoActor me = PotatoActor.this;
					me.remove();
					t.addActor(me);
					me.setPosition(80, 30);
					return true;
				}
			};
			
			SequenceAction moveSequence = new SequenceAction(flip, to, endMoveAction);
			this.addAction(moveSequence);
		}
		
		public Unit getUnit(){
			return this.gameUnit;
		}
	}