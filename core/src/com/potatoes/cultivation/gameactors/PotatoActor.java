package com.potatoes.cultivation.gameactors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.potatoes.cultivation.logic.Unit;

public class PotatoActor extends Actor {
		Unit gameUnit;
		
		private float stateTime;
		private Animation potatoAnim;
		
		private ActorAssets myAssets;
		
		/**
		 * 
		 * @param u - Unit
		 * @param a - ActorAssets
		 * @param c, either "yellow", "purple" or "red", or defaults to "yellow"
		 */
		public PotatoActor(Unit u, ActorAssets a, String c) {
			super();
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
		
		public void changeAnimation(Animation a) {
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
		
		public void testMove2(TileActor t) {
			MoveToAction to = new MoveToAction();
			
			// No checks yet (like whether the goto is left or right
			// just testing here
			ScaleToAction flip = new ScaleToAction();
			if(t.getX() > this.getParent().getX()) flip.setScale(-1, 1);
			this.addAction(flip);
			
			
			TileActor old = (TileActor) this.getParent();
			Vector2 oldpos = old.localToStageCoordinates(new Vector2(getX(), getY()));
			old.removeActor(this);
			
			t.addActor(this);
			oldpos = t.stageToLocalCoordinates(oldpos);
			this.setPosition(oldpos.x, oldpos.y);
			
			to.setDuration(3);
			
			to.setPosition(80, 30);
			to.setInterpolation(Interpolation.pow3Out);
			
			this.addAction(to);
		}
	}