package com.kesdip.player.components;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AnalogClockPanel extends JPanel {
	
	ClockPanel clockPanel;
	
	public void setAnalogClockPanel(ClockPanel clockPanel) {
		this.clockPanel = clockPanel;
	}
}
