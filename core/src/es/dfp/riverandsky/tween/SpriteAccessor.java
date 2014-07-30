package es.dfp.riverandsky.tween;

import aurelienribon.tweenengine.TweenAccessor;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteAccessor implements TweenAccessor<Sprite>{

	public static final int ALPHA = 0, SCALE = 2;
	
	@Override
	public int getValues(Sprite target, int tweenType, float[] returnValues) {
		switch(tweenType){
			case ALPHA:
				returnValues[0] = target.getColor().a;
				return 1;
			case SCALE:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;
		default:
			assert false;
			return -1;
		}
	}

	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
		switch(tweenType){
			case ALPHA:
				target.setColor(target.getColor().r, target.getColor().g, target.getColor().b, newValues[0]);
				break;
			case SCALE:
				target.setScale(newValues[0], newValues[1]);
			break;
			default:
				assert false;
		}
	}
	
	
}
