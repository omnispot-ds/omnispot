package com.kesdip.player.components.clock;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;


//import com.kesdip.common.util.ui.RepaintWorker;

@SuppressWarnings("serial")
public class ClockPanel extends JLayeredPane {

	//private static final Logger logger = Logger.getLogger(ClockPanel.class);

	public int timeHours;
	public int timeMinutes;
	public int timeSeconds;
	public int timeMillis;
	public int prefX;
	public int prefY;

	public BufferedImage baseImage;
	public String clockTitle;
	public boolean showSecondHand = true;
	public boolean antialias = true;
	// private Color bgColor;
	public boolean clockPainted = false;

	public int outerDiameter;
	public int centerXY;
	public Image image;
	public Color faceDark;
	public Color faceLight;
	public Color faceText;
	
	

	@Override
	public Component add(Component comp, int index) {
		if (comp instanceof AnalogClockPanel)
			((AnalogClockPanel)comp).setAnalogClockPanel(this);
		
		return super.add(comp, index);
		
	}

	public Dimension getPreferredSize() {
		return new Dimension(this.prefX, this.prefY);
	}

	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	public Dimension getMaximumSize() {
		return new Dimension(1000, 1000);
	}

	public void setSize(int paramInt1, int paramInt2) {
		super.setSize(paramInt1, paramInt2);
		int i = paramInt1;
		int j = paramInt2;

		i = (i > 1000) ? 1000 : i;
		i = (i < 100) ? 100 : i;
		j = (j > 1000) ? 1000 : j;
		j = (j < 100) ? 100 : j;

		if ((i != this.prefX) || (j != this.prefY)) {
			setSizeStuff(i, j);
		}
	}

	public void setSizeStuff(int paramInt1, int paramInt2) {
		int i = (paramInt2 > paramInt1) ? paramInt1 : paramInt2;
		outerDiameter = (i - 10);
		prefX = (this.prefY = i);
		if (outerDiameter < 10) {
			outerDiameter = 10;
		}
		centerXY = (i / 2);
	}


	/**
	 * @return the timeHours
	 */
	public int getTimeHours() {
		return timeHours;
	}

	/**
	 * @param timeHours the timeHours to set
	 */
	public void setTimeHours(int timeHours) {
		this.timeHours = timeHours;
	}

	/**
	 * @return the timeMinutes
	 */
	public int getTimeMinutes() {
		return timeMinutes;
	}

	/**
	 * @param timeMinutes the timeMinutes to set
	 */
	public void setTimeMinutes(int timeMinutes) {
		this.timeMinutes = timeMinutes;
	}

	/**
	 * @return the timeSeconds
	 */
	public int getTimeSeconds() {
		return timeSeconds;
	}

	/**
	 * @param timeSeconds the timeSeconds to set
	 */
	public void setTimeSeconds(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	/**
	 * @return the timeMillis
	 */
	public int getTimeMillis() {
		return timeMillis;
	}

	/**
	 * @param timeMillis the timeMillis to set
	 */
	public void setTimeMillis(int timeMillis) {
		this.timeMillis = timeMillis;
	}

	/**
	 * @return the prefX
	 */
	public int getPrefX() {
		return prefX;
	}

	/**
	 * @param prefX the prefX to set
	 */
	public void setPrefX(int prefX) {
		this.prefX = prefX;
	}

	/**
	 * @return the prefY
	 */
	public int getPrefY() {
		return prefY;
	}

	/**
	 * @param prefY the prefY to set
	 */
	public void setPrefY(int prefY) {
		this.prefY = prefY;
	}

	/**
	 * @return the clockTitle
	 */
	public String getClockTitle() {
		return clockTitle;
	}

	/**
	 * @param clockTitle the clockTitle to set
	 */
	public void setClockTitle(String clockTitle) {
		this.clockTitle = clockTitle;
	}

	/**
	 * @return the showSecondHand
	 */
	public boolean isShowSecondHand() {
		return showSecondHand;
	}

	/**
	 * @param showSecondHand the showSecondHand to set
	 */
	public void setShowSecondHand(boolean showSecondHand) {
		this.showSecondHand = showSecondHand;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the faceDark
	 */
	public Color getFaceDark() {
		return faceDark;
	}

	/**
	 * @param faceDark the faceDark to set
	 */
	public void setFaceDark(Color faceDark) {
		this.faceDark = faceDark;
	}

	/**
	 * @return the faceLight
	 */
	public Color getFaceLight() {
		return faceLight;
	}

	/**
	 * @param faceLight the faceLight to set
	 */
	public void setFaceLight(Color faceLight) {
		this.faceLight = faceLight;
	}

	/**
	 * @return the faceText
	 */
	public Color getFaceText() {
		return faceText;
	}

	/**
	 * @param faceText the faceText to set
	 */
	public void setFaceText(Color faceText) {
		this.faceText = faceText;
	}

}
