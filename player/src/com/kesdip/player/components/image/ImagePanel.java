package com.kesdip.player.components.image;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class ImagePanel extends JPanel {
	private static final Logger logger = Logger.getLogger(ImagePanel.class);

	private static final long serialVersionUID = 4895784829788904022L;

	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
		setLayout(null);
	}
	
	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	public void paintComponent(Graphics g) {
		logger.info("About to draw image on (" + getX() + "," + getY() + ") size (" +
				getWidth() + "," + getHeight() + ")");
		logger.info("Image dimensions are: (" + img.getWidth(null) + "," + img.getHeight(null) + ")");
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

}
