package com.kesdip.player.components;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.image.ImagePanel;
import com.kesdip.player.registry.ContentRegistry;

public class Image extends AbstractComponent implements InitializingBean {
	private static final Logger logger = Logger.getLogger(Image.class);
	
	/* SPRING STATE */
	private List<Resource> contents;
	private int duration;
	
	public void setContents(List<Resource> contents) {
		this.contents = contents;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (contents == null || contents.size() == 0)
			throw new Exception("Contents property has not been set.");
	}
	
	/* TRANSIENT STATE */
	private ImagePanel panel;
	private BufferedImage img;
	private int currentImageIndex;
	private long startTime;

	private void loadImage() throws ComponentException {
		ContentRegistry registry = ContentRegistry.getContentRegistry();
		String imageFilename = registry.getResourcePath(contents.get(currentImageIndex), true);
		logger.info("Loading image from file: " + imageFilename);
		try {
		    img = ImageIO.read(new File(imageFilename));
		} catch (IOException e) {
			throw new ComponentException(
					"Unable to load image: " + imageFilename, e);
		}
	}
	
	@Override
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		setPlayer(player);
		
		startTime = new Date().getTime();
		currentImageIndex = 0;
		loadImage();
		
		panel = new ImagePanel(img);
		panel.setLocation(x, y);
		panel.setBackground(backgroundColor);
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		logger.info("About to add image at: (" + x + "," + y +
				") with size: (" + width + "," + height + ")");
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
		if (duration == 0)
			return;
		
		long currentTime = new Date().getTime();
		if (currentTime - startTime < duration)
			return; // Nothing to see here. Move along now.
		
		// If we reach this line, then we must move on to the next image.
		currentImageIndex = (currentImageIndex + 1) % contents.size();
		loadImage();
		panel.setImage(img);
		startTime = new Date().getTime();
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.addAll(contents);
		return retVal;
	}

}
