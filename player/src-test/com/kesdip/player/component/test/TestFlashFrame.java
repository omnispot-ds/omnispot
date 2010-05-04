/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 16 Οκτ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.component.test;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author gerogias
 * 
 */
public class TestFlashFrame extends JFrame {

	private Canvas canvas = null;

	private final String PLAYER_EXE = "c:/tmp/gnash/gtk-gnash.exe";

	private final String SAMPLE_SWF = "c:/tmp/gnash/sample.swf";

	private TestFlashFrame() {
		setTitle("Flash test");
		setSize(500, 500);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().add(getFlashPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonsPanel(), BorderLayout.SOUTH);
		setBackground(Color.BLACK);
		addWindowListener(new WindowCloseListener());
	}

	private Component getFlashPanel() {
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		canvas = new Canvas();
		canvas.setBackground(Color.BLACK);
		panel1.add(canvas, BorderLayout.CENTER);
		return panel1;
	}

	private Component getButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		JButton playButton = new JButton();
		playButton.setAction(playAction);
		panel.add(playButton);
		return panel;
	}

	private Action playAction = new AbstractAction("Play") {

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			try {
				Runtime.getRuntime().exec(
						PLAYER_EXE + " -x "
								+ com.sun.jna.Native.getComponentID(canvas) + " "
								+ SAMPLE_SWF);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private class WindowCloseListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				// terminateFlash();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				System.exit(0);
			}
		}
	}
	public static void main(String[] args) throws Exception {
		TestFlashFrame frm = new TestFlashFrame();
		frm.setVisible(true);
	}
}
