package com.kesdip.player.components.image;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 4895784829788904022L;

	private Image img;

	public ImagePanel(Image img) {
		this.img = img;
		setLayout(null);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}
