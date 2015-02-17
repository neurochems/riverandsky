package es.dfc.riverandsky;

import java.lang.reflect.Field;

import com.badlogic.gdx.controllers.Controller;

/** Button and axis indices for the PS3 {@link Controller}.*/
public class PS3 {
	public static final String ID = "PS3 Game Controller";
	public static final int BUTTON_X;
	public static final int BUTTON_S;
	public static final int BUTTON_T;
	public static final int BUTTON_O;
	public static final int BUTTON_START;
	public static final int BUTTON_SELECT;
	public static final int BUTTON_DPAD_UP;
	public static final int BUTTON_DPAD_DOWN;
	public static final int BUTTON_DPAD_RIGHT;
	public static final int BUTTON_DPAD_LEFT;
	public static final int BUTTON_L1;
	public static final int BUTTON_L2;
	public static final int BUTTON_L3;
	public static final int BUTTON_R1;
	public static final int BUTTON_R2;
	public static final int BUTTON_R3;
	public static final int AXIS_LEFT_X;
	public static final int AXIS_LEFT_Y;
	public static final int AXIS_LEFT_TRIGGER;
	public static final int AXIS_RIGHT_X;
	public static final int AXIS_RIGHT_Y;
	public static final int AXIS_RIGHT_TRIGGER;
	public static final float STICK_DEADZONE = 0.25F;
	public static final int SYSTEM;
	
	static {
/*		boolean isOuya = false;
		try {
			Class<?> buildClass = Class.forName("android.os.Build");
			Field deviceField = buildClass.getDeclaredField("DEVICE");
			Object device = deviceField.get(null);
			isOuya = "ouya_1_1".equals(device) || "cardhu".equals(device);
		} catch (Exception e) {System.out.println(e);
		}*/

		BUTTON_X = 99;
		BUTTON_S = 96;
		BUTTON_T = 97;
		BUTTON_O = 100;
		BUTTON_START = 108;
		BUTTON_SELECT = 109;
		BUTTON_DPAD_UP = 19;
		BUTTON_DPAD_DOWN = 20;
		BUTTON_DPAD_RIGHT = 22;
		BUTTON_DPAD_LEFT = 21;
		BUTTON_L1 = 102;
		BUTTON_L2 = 12;
		BUTTON_L3 = 106;
		BUTTON_R1 = 103;
		BUTTON_R2 = 13;
		BUTTON_R3 = 107;
		AXIS_LEFT_X = 0;
		AXIS_LEFT_Y = 1;
		AXIS_LEFT_TRIGGER = 2;
		AXIS_RIGHT_X = 3;
		AXIS_RIGHT_Y = 4;
		AXIS_RIGHT_TRIGGER = 5;
		SYSTEM = 188;

	}
	
}

