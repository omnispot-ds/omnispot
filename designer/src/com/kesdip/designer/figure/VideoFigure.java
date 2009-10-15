package com.kesdip.designer.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.kesdip.designer.model.VideoComponent;

public class VideoFigure extends Figure implements PropertyChangeListener{
	
	private VideoComponent videoComponent;
	
	public VideoFigure(VideoComponent videoComponent) {
		this.videoComponent = videoComponent;
		this.videoComponent.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
	
	@Override
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds();
		g.setBackgroundColor(ColorConstants.green);
		g.setForegroundColor(ColorConstants.black);
		
		g.fillRectangle(r);
		g.drawLine(r.getTopLeft(), r.getBottomRight());
		g.drawLine(r.getBottomLeft(), r.getTopRight());
		
		Point center = r.getCenter();
		g.drawString("VIDEO", center.x - 15, center.y - 7);
	}
}
