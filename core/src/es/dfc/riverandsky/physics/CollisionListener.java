package es.dfc.riverandsky.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import es.dfc.riverandsky.screens.Game;

public class CollisionListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Object fixtureUserData = contact.getFixtureA().getUserData();
		System.out.println("fixtureUserDataA: " + fixtureUserData);
		if (fixtureUserData != null && (Integer) fixtureUserData == 3) {
			Game.numFootContacts++;
		}
		fixtureUserData = contact.getFixtureB().getUserData();
		if (fixtureUserData != null && (Integer) fixtureUserData == 3) {
			System.out.println("fixtureUserDataB: " + fixtureUserData);
			Game.numFootContacts++;
		}
	}

	@Override
	public void endContact(Contact contact) {
		Object fixtureUserData = contact.getFixtureA().getUserData();
		if (fixtureUserData != null && (Integer) fixtureUserData == 3) {
			Game.numFootContacts--;
		}
		fixtureUserData = contact.getFixtureB().getUserData();
		if (fixtureUserData != null && (Integer) fixtureUserData == 3) {
			Game.numFootContacts--;
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
