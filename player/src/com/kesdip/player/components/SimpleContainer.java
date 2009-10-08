/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//import com.kesdip.common.util.ui.RepaintWorker;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;

/**
 * A simple container component.
 * 
 * @author Pafsanias Ftakas
 */
public class SimpleContainer extends AbstractComponent {
	
	protected List<Component> contents;

	public void setContents(List<Component> contents) {
		this.contents = contents;
	}

	/* TRANSIENT STATE */
	private JPanel panel;
	
	/**
	 * The repaint worker.
	 */
//	private RepaintWorker repaintWorker = null;
	
	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		setPlayer(player);
		
		panel = new JPanel();
		panel.setLocation(x, y);
		if (backgroundColor != null) {
			panel.setBackground(backgroundColor);
		} else {
			panel.setOpaque(false);
		}
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		
		for (Component component : contents) {
			component.init(this, timingMonitor, player);
		}
		
//		repaintWorker = new RepaintWorker(panel);
		
		parent.add(this);
	}

	@Override
	public void add(Component component) throws ComponentException {
		java.awt.Component windowComponent = component.getWindowComponent();
		if (windowComponent == null) {
			return;
		}
		
		panel.add(windowComponent);
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return panel;
	}

	@Override
	public void repaint() throws ComponentException {
		for (Component component : contents) {
			component.repaint();
		}
		panel.repaint();
	}

	@Override
	public CompletionStatus isComplete() {
		for (Component component : contents) {
			switch (component.isComplete()) {
			case COMPLETE:
				return CompletionStatus.COMPLETE;
			case INCOMPLETE:
				return CompletionStatus.INCOMPLETE;
			case DONT_CARE:
				// Do nothing
				break;
			default:
				throw new RuntimeException("Unexpected completion " +
						"state: " + component.isComplete());
			}
		}
		
		return CompletionStatus.DONT_CARE;
	}

	@Override
	public void releaseResources() {
		for (Component component : contents) {
			component.releaseResources();
		}
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		for (Component component : contents) {
			retVal.addAll(component.gatherResources());
		}
		return retVal;
	}

}
