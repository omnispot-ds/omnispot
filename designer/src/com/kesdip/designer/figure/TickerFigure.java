package com.kesdip.designer.figure;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.kesdip.designer.model.TickerComponent;

public class TickerFigure extends Figure implements PropertyChangeListener {
	private TickerComponent tickerComponent;
	
	public TickerFigure(TickerComponent tickerComponent) {
		this.tickerComponent = tickerComponent;
		this.tickerComponent.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
	
	@Override
	protected void paintFigure(Graphics g) {
		Device d = Display.getCurrent();
		Rectangle r = getBounds();
		Color oldFore = g.getForegroundColor();
		Color oldBack = g.getBackgroundColor();
		Font oldFont = g.getFont();
		RGB compFore = (RGB)
			tickerComponent.getPropertyValue(TickerComponent.FOREGROUND_COLOR_PROP);
		RGB compBack = (RGB)
			tickerComponent.getPropertyValue(TickerComponent.BACK_COLOR_PROP);
		Boolean isTransparent = (Boolean)
			tickerComponent.getPropertyValue(TickerComponent.TRANSPARENT_PROP);
		java.awt.Font compFont = (java.awt.Font)
			tickerComponent.getPropertyValue(TickerComponent.FONT_PROP);
		Color newFore = new Color(d, compFore);
		Color newBack = new Color(d, compBack);
		int style = SWT.NONE;
		if (compFont.isBold())
			style &= SWT.BOLD;
		if (compFont.isItalic())
			style &= SWT.ITALIC;
		Font newFont;
		if (tickerComponent.getFontData() == null) {
			newFont = new Font(d, compFont.getFamily(), style, compFont.getSize());
		} else {
			newFont = new Font(d, tickerComponent.getFontData());
		}
		
		if (isTransparent) {
			g.setBackgroundColor(ColorConstants.lightBlue);
		} else {
			g.setBackgroundColor(newBack);
		}
		g.setForegroundColor(newFore);
		g.setFont(newFont);
		
		g.fillRectangle(r);
		g.drawLine(r.getTopLeft(), r.getBottomRight());
		g.drawLine(r.getBottomLeft(), r.getTopRight());
		
		String textToDisplay = "Ticker Component";
		
		if (tickerComponent.getPropertyValue(TickerComponent.TYPE_PROP).equals(0)) {
			textToDisplay = (String) tickerComponent.getPropertyValue(
					TickerComponent.STRING_PROP);
		}
		
		Point center = r.getCenter();
		g.drawText(textToDisplay, r.x, center.y - g.getFontMetrics().getHeight() / 2);
		
		g.setForegroundColor(oldFore);
		g.setBackgroundColor(oldBack);
		g.setFont(oldFont);

		newFore.dispose();
		newBack.dispose();
		newFont.dispose();
	}
}
