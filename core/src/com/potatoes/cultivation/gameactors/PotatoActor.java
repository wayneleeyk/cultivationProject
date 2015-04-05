package com.potatoes.cultivation.gameactors;

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
import com.potatoes.cultivation.logic.Unit;

public class PotatoActor extends Actor {
		Unit gameUnit;
		
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
			String color = c;
			if(!(color.equalsIgnoreCase("yellow") || color.equalsIgnoreCase("red") || color.equalsIgnoreCase("purple"))) {
				color = "yellow";
			}
			
			potatoAnim = a.stringToAnimation.get("potato" + "_" + color.toLowerCase() + "_" + type.toLowerCase());
			int width = potatoAnim.getKeyFrame(0).getRegionWidth();
			int height = potatoAnim.getKeyFrame(0).getRegionHeight();
			this.setWidth(width);
			this.setHeight(height);
			this.setOriginX(width/2);
			
			// Add listener to forward to ClickManager
			this.addListener(cm.new ClickToCMListener());
		}
		
		@Override
		public void act(float delta) {
			stateTime += delta;
			super.act(delta);
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			TextureRegion currentFrame = potatoAnim.getKeyFrame(stateTime, true);
			batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
			super.draw(batch, parentAlpha);
		}
		
		/**
		 * Directly set the animation to a. Use for debugging
		 * @param a
		 */
		public void setAnimation(Animation a) {
			potatoAnim = a;
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
		
		public void testMove2(final TileActor t) {
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
			
			to.setDuration(3);
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
		
		public void testUpgrade() {
			setAnimation(myAssets.potato_yellow_soldier);
		}
		
		public Unit getUnit(){
			return this.gameUnit;
		}
	}