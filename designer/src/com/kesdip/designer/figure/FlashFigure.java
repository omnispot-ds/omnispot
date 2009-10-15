package com.kesdip.designer.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.kesdip.designer.model.FlashComponent;

public class FlashFigure extends Figure implements PropertyChangeListener {
	private FlashComponent flashComponent;
	
	public FlashFigure(FlashComponent flashComponent) {
		this.flashComponent = flashComponent;
		this.flashComponent.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
	
	@Override
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds();
		g.setBackgroundColor(ColorConstants.gray);
		g.setForegroundColor(ColorConstants.black);
		
		g.fillRectangle(r);
		g.drawLine(r.getTopLeft(), r.getBottomRight());
		g.drawLine(r.getBottomLeft(), r.getTopRight());
		
		Point center = r.getCenter();
		g.drawString("FLASH", center.x - 16, center.y - 7);
	}

}
