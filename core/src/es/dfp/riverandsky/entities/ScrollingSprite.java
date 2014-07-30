package es.dfp.riverandsky.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import es.dfp.riverandsky.entities.Player;
import es.dfp.riverandsky.screens.Game;

public class ScrollingSprite extends Sprite {
	
	private float aniTime = 0;
	
	private Animation ani;
	
	private Texture texture;
	
	/** Animated */
	public ScrollingSprite (Animation animation) {
		super (animation.getKeyFrame(0));
		this.ani = animation;
	}

	/** Static */
	public ScrollingSprite (Texture texture) {
		super (texture);
		this.texture = texture;
	}
	
	public void drawAnimated (Batch batch, float x, float y, float scale) {
		update(Gdx.graphics.getDeltaTime());
		super.draw(batch);
		super.setX(x);
		super.setY(y);
		super.setScale(scale);
	}

	public void drawStatic (Batch batch, float x, float y, float scale) {
		updateStatic();
		super.draw(batch);
		super.setX(x);
		super.setY(y);
		super.setScale(scale);
	}
	
	public void update (float delta) {
		aniTime += delta;
		setRegion(ani.getKeyFrame(aniTime));
	}

	public void updateStatic () {
		setRegion(texture);
	}

}
