package com.kesdip.player.components.clock;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;


@SuppressWarnings("serial")
class DigitalClockPanel extends JPanel {

	DateFormat dateFormat =new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");;

	public void setDateFormat(String dateFormat) {
		this.dateFormat =new SimpleDateFormat(dateFormat);
	}
	protected Color foregroundColor;
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font.deriveFont(100f);
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	boolean firstPass = true;
	int fMsgX = 0, fMsgY = 0;
	Font font = new Font ("Serif", Font.BOLD, 100);

	
	public void paintComponent (Graphics g) {
		super.paintComponent (g);

		Date now = new Date ();

		String date = dateFormat.format (now);
		g.setColor(foregroundColor);
		g.setFont(font);

		// Do the size and placement calculations only for the
		// first paint  
		if (firstPass) {

			adjustFontSize(g,date);
			FontMetrics fm = g.getFontMetrics ();
			int datestr_width = fm.stringWidth (date);
			// Use the string width to find the starting point
			fMsgX = getSize ().width/2 - datestr_width/2;

			// How far above the baseline can the font go?
			int ascent = fm.getMaxAscent ();

			// How far below the baseline?
			int descent= fm.getMaxDescent ();

			// Use the vertical height of this font to find
			// the vertical starting coordinate
			fMsgY = getSize ().height/2 - descent/2 + ascent/2;
		}
		g.drawString (date,fMsgX,fMsgY);
		firstPass = false;
	} 

	private void adjustFontSize(Graphics g, String date){
		FontMetrics fm = g.getFontMetrics ();
		int datestr_width = fm.stringWidth (date);
		if (getSize().getWidth()<datestr_width) {
			font = font.deriveFont(Float.valueOf(font.getSize()-1));
			g.setFont(font);
			adjustFontSize(g, date);
		}
	}
} 
