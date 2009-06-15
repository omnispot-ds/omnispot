/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.helpers;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import com.kesdip.player.Player;
import com.kesdip.player.components.Resource;
import com.kesdip.player.constenum.ResourceAttributeKeys;

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
			Image image = toolkit.createImage(new byte[] { 0 });
			noCursor = toolkit.createCustomCursor(image, new Point(1, 1),
					"blank cursor");
		}

		return noCursor;
	}

	private static Map<Player, ExitKeyListener> exitKeyListenerMap;

	public static synchronized ExitKeyListener getExitKeyListener(Player player) {
		if (exitKeyListenerMap == null) {
			exitKeyListenerMap = new HashMap<Player, ExitKeyListener>();
		}

		ExitKeyListener exitKeyListener = exitKeyListenerMap.get(player);

		if (exitKeyListener == null) {
			exitKeyListener = new ExitKeyListener(player);
			exitKeyListenerMap.put(player, exitKeyListener);
		}

		return exitKeyListener;
	}

	/**
	 * Check if the resource is marked as full-screen.
	 * 
	 * @param resource
	 *            the resource to check
	 * @return boolean the value of the attribute or <code>false</code> by
	 *         default
	 */
	public static boolean isResourceFullScreen(Resource resource) {
		if (resource == null) {
			return false;
		}
		if (!resource.getAttributes().containsKey(
				ResourceAttributeKeys.FULL_SCREEN)) {
			return false;
		}
		String value = resource.getAttributes().get(
				ResourceAttributeKeys.FULL_SCREEN);
		return "true".equalsIgnoreCase(value);
	}
}
