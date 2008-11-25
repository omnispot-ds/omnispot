package com.kesdip.player.helpers;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * Various utility methods.
 * 
 * @author Pafsanias Ftakas
 */
public class PlayerUtils {
	private static Cursor noCursor;
	
	public static synchronized Cursor getNoCursor() {
		if (noCursor == null) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.createImage(new byte[]{0});
			noCursor = toolkit.createCustomCursor(image, new Point(1,1),
			                                             "blank cursor");
		}
		
		return noCursor;
	}
	
	private static ExitKeyListener exitKeyListener;
	
	public static synchronized ExitKeyListener getExitKeyListener() {
		if (exitKeyListener == null) {
			exitKeyListener = new ExitKeyListener();
		}
		
		return exitKeyListener;
	}
}
