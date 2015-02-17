/*  River and Sky: The Game
 *  Code by: Brendan Lehman
 *  Art by: Holly Robin
 *  Audio Production: Near North Mobile Media Lab (Holly Cunningham, Ben Leggett)
 *  Test version start date (as Consciousness): Thursday, March 13, 2014
 *  Project start date: July 3, 2014
 */

package es.dfc.riverandsky;

import com.badlogic.gdx.Game;

public class RiverAndSky extends Game {
	
	@Override
	public void create() {
		setScreen(new es.dfc.riverandsky.screens.Game());
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void resize( int width, int height) {
		super.resize(width, height);
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}
}
