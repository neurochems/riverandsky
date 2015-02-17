package es.dfp.riverandsky.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import es.dfp.riverandsky.RiverAndSky;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.vSyncEnabled = true;
		cfg.width = 1920;
		cfg.height = 1080;
		cfg.fullscreen = true;
		new LwjglApplication(new RiverAndSky(), cfg);
	}
}
