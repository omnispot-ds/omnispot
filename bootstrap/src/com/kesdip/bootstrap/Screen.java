package com.kesdip.bootstrap;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Screen {
	
	public static void grabAndSaveToFile() throws Exception {
		
		Robot robot = new Robot();
		BufferedImage screenShot = 
			robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		int newWidth = screenShot.getWidth() * 480 / screenShot.getHeight();
		Image screen = screenShot.getScaledInstance(newWidth, 480, Image.SCALE_FAST);
		
		screenShot = new BufferedImage(newWidth,480,BufferedImage.TYPE_INT_RGB);
		screenShot.createGraphics().drawImage(screen, 0, 0, null);
		File parentFolder = new File(Config.getSingleton().getScreenShotStorageLocation());
		// make sure it exists
		if (!parentFolder.isDirectory()) {
			parentFolder.mkdirs();
		}
		ImageIO.write(screenShot, "JPG", new File(parentFolder, "screenShot.jpg"));
	}
}
