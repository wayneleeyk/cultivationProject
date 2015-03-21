package com.potatoes.cultivation.gui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.potatoes.cultivation.logic.Unit;

public class PotatoActor extends Actor {
		Unit gameUnit;
		
		private float stateTime;
		private Animation potatoAnim;
		
		public PotatoActor(Unit u, Animation potato) {
			super();
			gameUnit = u;
			potatoAnim = potato;
			int width = potato.getKeyFrame(0).getRegionWidth();
			int height = potato.getKeyFrame(0).getRegionHeight();
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
	}