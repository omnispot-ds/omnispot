package com.kesdip.designer.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.utils.DesignerLog;

public class ImageFigure extends org.eclipse.draw2d.ImageFigure
		implements PropertyChangeListener {
	private ImageComponent imageComponent;
	private boolean isImageBroken;
	
	public ImageFigure(ImageComponent imageComponent) {
		this.imageComponent = imageComponent;
		this.imageComponent.addPropertyChangeListener(this);
		updateImage();
	}
	
	protected void updateImage() {
		if (getImage() != null) {
			getImage().dispose();
		}
		Device d = Display.getCurrent();
		if (imageComponent.getImages().size() == 0) {
			setImage(null);
			isImageBroken = false;
		} else {
			String imageFilename = imageComponent.getImages().get(0).getResource();
			try {
				Image image = new Image(d, imageFilename);
				setImage(image);
				isImageBroken = false;
			} catch (SWTException e) {
				DesignerLog.logError("Unable to read image from file: " +
						imageFilename, e);
				setImage(null);
				isImageBroken = true;
			}
		}
	}

	@Override
	protected void paintFigure(Graphics g) {
		if (getImage() != null && !isImageBroken) {
			super.paintFigure(g);
			return;
		}
		
		Rectangle r = getBounds();
		g.setBackgroundColor(ColorConstants.orange);
		g.setForegroundColor(ColorConstants.black);
		
		g.fillRectangle(r);
		g.drawLine(r.getTopLeft(), r.getBottomRight());
		g.drawLine(r.getBottomLeft(), r.getTopRight());
		
		Point center = r.getCenter();
		if (isImageBroken)
			g.drawString("IMAGE (BROKEN)", center.x - 46, center.y - 7);
		else
			g.drawString("IMAGE", center.x - 17, center.y - 7);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ImageComponent.IMAGE_PROP) ||
				evt.getPropertyName().equals(ImageComponent.SIZE_PROP))
			updateImage();
		
		repaint();
	}
}
