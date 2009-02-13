package com.kesdip.player.components;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.image.ImagePanel;
import com.kesdip.player.registry.ContentRegistry;

public class Image extends AbstractComponent implements InitializingBean {
	private static final Logger logger = Logger.getLogger(Image.class);
	
	private Resource image;
	
	public void setImage(Resource image) {
		this.image = image;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (image == null)
			throw new Exception("Image property has not been set.");
	}
	
	/* TRANSIENT STATE */
	private ImagePanel panel;
	private BufferedImage img;

	@Override
	public void init(Component parent, TimingMonitor timingMonitor)
			throws ComponentException {
		ContentRegistry registry = ContentRegistry.getContentRegistry();
		String imageFilename = registry.getResourcePath(image);
		logger.info("Loading image from file: " + imageFilename);
		try {
		    img = ImageIO.read(new File(imageFilename));
		} catch (IOException e) {
			throw new ComponentException(
					"Unable to load image: " + imageFilename, e);
		}
		
		panel = new ImagePanel(img);
		panel.setLocation(x, y);
		panel.setBackground(backgroundColor);
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		parent.add(this);
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("Image component is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return panel;
	}

	@Override
	public void repaint() throws ComponentException {
		// Intentionally do nothing.
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.add(image);
		return retVal;
	}

}
