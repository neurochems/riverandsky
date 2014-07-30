package es.dfp.riverandsky.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import es.dfp.riverandsky.PS3;
import es.dfp.riverandsky.screens.Game;

public class Player extends Sprite implements InputProcessor, ControllerListener, ContactFilter, ContactListener {

	private Body body;
	private Fixture fixture;
	
	private final float WIDTH, HEIGHT;
	
	private float movementForce = 500, jumpPower = 45;
	
	private Vector2 velocity = new Vector2();
	
	private float speed = 60 * 4.5f, gravity = 60 * 3.2f, aniTime;
	private int scrollSpeed = 4;
	
	private boolean canJump;
	
	private Animation run, jumpUp, jumpDown, idle;
	private Animation runL, jumpUpL, jumpDownL, idleL;

	private TiledMapTileLayer collisionLayer, pickupLayer;
	private String blockedKey = "blocked", pickupKey = "animation", endKey = "stop", fadeKey = "fadeout";
	
	private static int score = 0;

	private boolean jumping = false, falling = false, standing = true, left = false, menu = false, grounded = false;
	private float ground; 	//ground level, in px
	
	/** Player for the game */
	public Player(World world, float x, float y, float width, Animation run, Animation jumpUp, Animation jumpDown, Animation idle, TiledMapTileLayer pickupLayer) {
		super (idle.getKeyFrame(0)); 
		
		WIDTH = width;
		HEIGHT = width;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = true;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, HEIGHT / 2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0.8f;
		fixtureDef.density = 3;
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		
		this.run = run;
		this.jumpUp = jumpUp;
		this.jumpDown = jumpDown;
		this.idle = idle;
		this.pickupLayer = pickupLayer;
	}
	
	/** Player for the menu */
	public Player(World world, float x, float y, float width, Animation run, Animation runL, Animation jumpUp, Animation jumpDown, Animation idle, Animation idleL) {
		super (idle.getKeyFrame(0)); 
		
		WIDTH = width;
		HEIGHT = width;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);
		bodyDef.fixedRotation = true;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, HEIGHT / 2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.restitution = 0;
		fixtureDef.friction = 0.8f;
		fixtureDef.density = 3;
		
		body = world.createBody(bodyDef);
		fixture = body.createFixture(fixtureDef);
		
		this.run = run;
		this.jumpUp = jumpUp;
		this.jumpDown = jumpDown;
		this.idle = idle;
		this.runL = runL;
		this.idleL = idleL;
	}
	
	@Override
	public void draw(Batch spriteBatch) {
		body.applyForceToCenter(velocity, true);
		update(Gdx.graphics.getDeltaTime());
		super.draw(spriteBatch);

	}
	
	public void update(float delta) {
		//applies gravity
		velocity.y -= gravity * delta;
		
		//clamp velocity
		if (velocity.y > speed)
			velocity.y = speed;
		else if (velocity.y < -speed)
			velocity.y = -speed;
		
		
		//TILE COLLISION\\
		
		//save old positions
		float oldX = getX(), oldY = getY(), tileWidthC = collisionLayer.getTileWidth(), tileHeightC = collisionLayer.getTileHeight();
		boolean collisionY = false, collisionTR = false, collisionMR = false, collisionBR = false, collisionEndTR = false, collisionEndMR = false, collisionEndBR = false, collisionFadeTR = false, collisionFadeMR = false, collisionFadeBR = false;
		
		//move on X
		if (menu) {
			if (!left && !standing) setX(getX() + 2);
			if (left && !standing) setX(getX() - 2);
		}
		
		if (!menu) {
			
			setX(getX() + scrollSpeed);  //INFINITE RUNNING SPEED
			
			if (getX() - oldX == scrollSpeed) {	//going right
				//top right
				collisionTR = isCellPickup(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (( getY()) / tileHeightC)));
				collisionEndTR = isCellEnd(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (( getY()) / tileHeightC)));
				collisionFadeTR = isCellFade(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (( getY()) / tileHeightC)));
				
				//middle right
				if (!collisionTR) 
					collisionMR = isCellPickup(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight() / 2) / tileHeightC)));
					collisionEndMR = isCellEnd(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight() / 2) / tileHeightC)));
					collisionFadeMR = isCellFade(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight() / 2) / tileHeightC)));
				
				//bottom right
				if (!collisionTR && !collisionMR)
					collisionBR = isCellPickup(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
					collisionEndBR = isCellEnd(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
					collisionFadeBR = isCellFade(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
	
			}
			
			//react to X collision
			if (collisionTR) {
				System.out.println("collisionTR: " + collisionTR);
				updateScore(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (getY() / tileHeightC)));
				pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (getY() / tileHeightC))
					.setTile(pickupLayer.getCell(0, 0).getTile());
			}
			
			if (collisionEndTR) {
				//scrollSpeed = 0;
				Game.setEnd(true);
			}
			
			if (collisionFadeTR) {
				//scrollSpeed = 0;
				Game.setFadeOut(true);
			}

			if (collisionMR) {
				System.out.println("collisionMR: " + collisionMR);
				updateScore(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((1260 - getY()) / tileHeightC)));
				pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((1260 - getY() + getHeight() / 2) / tileHeightC))
					.setTile(pickupLayer.getCell(0, 0).getTile());
			}

			if (collisionEndMR) {
				//scrollSpeed = 0;
				Game.setEnd(true);
			}
			
			if (collisionFadeMR) {
				//scrollSpeed = 0;
				Game.setFadeOut(true);
			}
			
			if (collisionBR) {
				System.out.println("collisionBR: " + collisionBR);
				updateScore(pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
				pickupLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC))
					.setTile(pickupLayer.getCell(0, 0).getTile());
			}
			
			if (collisionEndBR) {
				Game.setEnd(true);
				scrollSpeed = 0;
			}
			
			if (collisionFadeBR) {
				//scrollSpeed = 0;
				Game.setFadeOut(true);
			}
			System.out.println("score: " + score);
			System.out.println("x: " + (getX() + getWidth()) / tileWidthC);
			System.out.println("y: " + ((1260 - getY()) / tileHeightC));
			System.out.println("X: " + pickupLayer.getCell(26, 12).getTile().getProperties().containsKey(blockedKey));
		}
		
		//move on Y
		setY(getY() + velocity.y * delta);
		
		if (velocity.y < 0) {	//going down
			//bottom left
			collisionY = isCellBlocked(collisionLayer.getCell((int) (getX() / tileWidthC), (int) (getY() / tileHeightC)));
			
			//bottom middle
			if (!collisionY)
				collisionY = isCellBlocked(collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tileWidthC), (int) (getY() / tileHeightC)));
			
			//bottom right
			if (!collisionY)
				collisionY = isCellBlocked(collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) (getY() / tileHeightC)));
			
			canJump = collisionY;
			//jumping = !collisionY;
		}
		else if (velocity.y > 0) {	//going up
			//top left
			collisionY = isCellBlocked(collisionLayer.getCell((int) (getX() / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
			
			//top middle
			if (!collisionY)
				collisionY = isCellBlocked(collisionLayer.getCell((int) ((getX() + getWidth() / 2) / tileWidthC), (int) ((getY() + getHeight() / 2) / tileHeightC)));
			
			//top right
			if (!collisionY)
				collisionY = isCellBlocked(collisionLayer.getCell((int) ((getX() + getWidth()) / tileWidthC), (int) ((getY() + getHeight()) / tileHeightC)));
		}

		//react to Y collision
		if (collisionY) {
			velocity.y = 0;			
			grounded = true;		//on the ground
			setY(oldY);
			}

		//set position states
	
		if (canJump) {
			falling = false;		//stop falling
			ground = getY();		//set current ground level (y position)
		}
		
		if (!falling && getY() < oldY && getY() < ground + 90) { 		//moving down, 90 px above last known ground
			falling = true;												//start falling animation
			jumping = false;
			aniTime = 0;												//restart animation time when start falling
		}
		
		//System.out.println("jumping: " + jumping);
		//System.out.println("falling: " + falling);
		//System.out.println("current Y: " + getY());
		//System.out.println("old Y: " + oldY);
		
		//OBJECT COLLISION\\
		
		/*if (getBoundingRectangle().overlaps(item.getBoundingRectangle()) && !itemCollision) {
			item.isCollected();
			itemCollision = true;
		}*/
		
		aniTime += delta;

		if (jumping) setRegion(jumpUp.getKeyFrame(aniTime));
		else if (falling && !grounded) setRegion(jumpDown.getKeyFrame(aniTime));
		else if (menu && !standing && grounded && left) setRegion(runL.getKeyFrame(aniTime));
		else if (menu && !standing && grounded) setRegion(run.getKeyFrame(aniTime));
		else if (menu && standing && grounded && left) setRegion(idleL.getKeyFrame(aniTime));
		else if (menu && standing && grounded) setRegion(idle.getKeyFrame(aniTime));
		//else if (jumping && oldY == 0 && velocity.y > -6 && velocity.y < -1) setRegion(run.getKeyFrame(aniTime));
		else if (grounded && !menu) setRegion(run.getKeyFrame(aniTime));
		else if (grounded && menu) setRegion(idle.getKeyFrame(aniTime));
		else if (grounded && menu && left) setRegion(idleL.getKeyFrame(aniTime));
		
	}

	// update checks
	private boolean isCellBlocked(Cell cell) {
		return cell.getTile() != null && cell.getTile().getProperties().containsKey(blockedKey);
	}
	private boolean isCellPickup(Cell cell) {
		System.out.println("isCellPickup()");
		return cell.getTile() != null && cell.getTile().getProperties().containsKey(pickupKey);
	}
	private boolean isCellEnd(Cell cell) {
		System.out.println("isCellEnd()");
		return cell.getTile() != null && cell.getTile().getProperties().containsKey(endKey);
	}
	private boolean isCellFade(Cell cell) {
		System.out.println("isCellFade()");
		return cell.getTile() != null && cell.getTile().getProperties().containsKey(fadeKey);
	}

	//SCORING\\
	
	private void updateScore(Cell cell) {
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("rasp")) {
			score += 100;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("orange")) {
			score += 200;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("bluit")) {
			score += 300;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("kiwi")) {
			score += 400;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("apple")) {
			score += 500;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("lemon")) {
			score += 600;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("melon")) {
			score += 700;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("grilled")) {
			score += 1000;
		}
		if (cell.getTile().getProperties().containsKey("animation") && cell.getTile().getProperties().get("animation", String.class).equals("cheesus")) {
			score += 5000;
		}
	}
	
	//INPUT\\

	//keyboard
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.UP:
			if (canJump && !menu) {
				velocity.y = speed;
				jumping = true;
				canJump = false;
				falling = false;
				grounded = false;
				aniTime = 0;
				System.out.println("jump");
			}
			break;
		case Keys.RIGHT:
			if (menu) {
				setX(getX() + 4);
				standing = false;
				left = false;
				aniTime = 0;}
			break;
		case Keys.LEFT:
			if (menu) {
				setX(getX() - 4);
				left = true;
				standing = false;
				aniTime = 0;
			}
			break;
		}
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.RIGHT:
			if (menu) {
				standing = true;
				aniTime = 0;
			}
			break;
		case Keys.LEFT:
			if (menu) {
				standing = true;
				aniTime = 0;
			}
			break;
		case Keys.ESCAPE:
			Gdx.app.exit();
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
	@Override
	public boolean scrolled(int amount) {

		
		return false;
	}

	//controller
	
	@Override
	public void connected(Controller controller) {
		
	}
	@Override
	public void disconnected(Controller controller) {
		
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (buttonCode == PS3.BUTTON_X && canJump && !menu) {
			velocity.y = speed;
			jumping = true;
			canJump = false;
			falling = false;
			grounded = false;
			aniTime = 0;
			System.out.println("jump");
		}
		if (buttonCode == PS3.BUTTON_DPAD_RIGHT) {
			if (menu) {
				setX(getX() + 4);
				standing = false;
				left = false;
				aniTime = 0;}
		}
		if (buttonCode == PS3.BUTTON_DPAD_LEFT) {
			if (menu) {
				setX(getX() - 4);
				left = true;
				standing = false;
				aniTime = 0;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		if (buttonCode == PS3.BUTTON_DPAD_RIGHT) {
			if (menu) {
				standing = true;
				aniTime = 0;
			}
	}
	if (buttonCode == PS3.BUTTON_DPAD_LEFT) {
		if (menu) {
			standing = true;
			aniTime = 0;
		}
	}
	if (buttonCode == PS3.BUTTON_T) {
		Gdx.app.exit();
	}
	
	return false;
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return false;
	}
	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		return false;
	}
	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		return false;
	}
	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		return false;
	}
	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		return false;
	}
	
	//GETTERS&SETTERS
	
	public Vector2 getVelocity() {
		return velocity;
	}
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	public boolean isStanding() {
		return standing;
	}
	public void setStanding(boolean standing) {
		this.standing = standing;
	}
	public boolean isMenuMode() {
		return menu;
	}
	public void setMenuMode(boolean menu) {
		this.menu = menu;
	}

	public static int getScore() {
		return score;
	}
	public static void setScore(int score) {
		Player.score = score;
	}

	public int getScrollSpeed() {
		return scrollSpeed;
	}
	public void setScrollSpeed(int scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	
	
}