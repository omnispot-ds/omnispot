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

import com.kesdip.designer.model.ClockComponent;
import com.kesdip.designer.model.Resource;
import com.kesdip.designer.utils.DesignerLog;

public class ClockFigure extends org.eclipse.draw2d.ImageFigure
		implements PropertyChangeListener {
	private ClockComponent clockComponent;
	private boolean isImageBroken;
	
	public ClockFigure(ClockComponent clockComponent) {
		this.clockComponent = clockComponent;
		this.clockComponent.addPropertyChangeListener(this);
		updateImage();
	}
	
	protected void updateImage() {
		if (getImage() != null) {
			getImage().dispose();
		}
		Device d = Display.getCurrent();
		if (clockComponent.getPropertyValue(ClockComponent.IMAGE_PROP) == null) {
			setImage(null);
			isImageBroken = false;
		} else {
			String imageFilename = ((Resource) clockComponent.getPropertyValue(
					ClockComponent.IMAGE_PROP)).getResource();
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
			g.drawString("CLOCK (BROKEN)", center.x - 46, center.y - 7);
		else
			g.drawString("CLOCK", center.x - 17, center.y - 7);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ClockComponent.IMAGE_PROP))
			updateImage();
		
		repaint();
	}
}
