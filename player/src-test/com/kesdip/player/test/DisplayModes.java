package com.kesdip.player.test;

import java.awt.*;
import javax.swing.*;
import java.util.Random;

public class DisplayModes {

  public static void main(String args[]) {
    GraphicsEnvironment graphicsEnvironment =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice graphicsDevice =
      graphicsEnvironment.getDefaultScreenDevice();
    DisplayMode displayModes[] = graphicsDevice.getDisplayModes();
    DisplayMode originalDisplayMode = graphicsDevice.getDisplayMode();
    JWindow window = new JWindow() {
      public void paint(Graphics g) {
        g.setColor(Color.blue);
        g.drawString("Hello, World!", 50, 50);
      }
    };
    try {
      if (graphicsDevice.isFullScreenSupported()) {
        graphicsDevice.setFullScreenWindow(window);
      }
      for (int i = 0; i < displayModes.length; i++) {
	      DisplayMode displayMode = displayModes[i];
	      System.out.println(displayMode.getWidth() + "x" + 
	        displayMode.getHeight() + " \t" + displayMode.getRefreshRate() + 
	        " / " + displayMode.getBitDepth());
	      if (graphicsDevice.isDisplayChangeSupported()) {
	        graphicsDevice.setDisplayMode(displayMode);
	      }
	      Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
    } finally {
      graphicsDevice.setDisplayMode(originalDisplayMode);
      graphicsDevice.setFullScreenWindow(null);
    }
    System.exit(0);
  }
}