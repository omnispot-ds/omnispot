/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Level;

/**
 * @author gerogias
 * 
 */
public class CopyOfTestFrame extends JFrame implements ProcessExitListener {

	private Canvas canvas1 = null;
	
	private Canvas canvas2 = null;

	private PrintStream embedded1MPlayerIn = null;

	private PrintStream embedded2MPlayerIn = null;

	private PrintStream fsMPlayerIn = null;
	
	private PrintStream tvMPlayerIn = null;
	
	private JButton play1_1Button = null;

	private JButton play1_2Button = null;

	private JButton play2_1Button = null;
	
	private JButton play2_2Button = null;
	
	private JButton pause1Button = null;
	
	private JButton pause2Button = null;
	
	private JButton pos1Button = null;
	
	private JButton pos2Button = null;
	
	private JButton tvButton = null;

	private final String MPLAYER_EXE = System.getProperty("MPLAYER_EXE");

	private final String VIDEO1 = "C:/Documents and Settings/gerogias/Desktop/mail/Heineken - Walkin Fridge.wmv";
	
	private final String VIDEO2 = "C:/Documents and Settings/gerogias/Desktop/mail/SRG9881.wmv";
	
	private final String VIDEO3 = "C:/Documents and Settings/gerogias/Desktop/mail/promenade(ch).wmv";
	
	private final String CHANNEL = "48";
	
	private final String CHANNELS_CONF = "C:/dbin/MPlayer-tv-directx/channels.conf";

	private CopyOfTestFrame() {
		setTitle("MPlayer test");
		setSize(700, 700);
		setResizable(false);
		setLayout(new BorderLayout());
		getContentPane().add(getPlayerPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonsPanel(), BorderLayout.SOUTH);
		setBackground(Color.BLACK);
		addWindowListener(new WindowCloseListener());
	}

	private Component getPlayerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		canvas1 = new Canvas();
		canvas1.setBackground(Color.BLACK);
		panel1.add(canvas1, BorderLayout.CENTER);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		canvas2 = new Canvas();
		canvas2.setBackground(Color.WHITE);
		panel2.add(canvas2, BorderLayout.CENTER);
		
		panel.add(panel1);
		panel.add(panel2);
		return panel;
	}

	private Component getButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(getButtons1());
		panel.add(getButtons2());
		return panel;
	}
	
	private Component getButtons1() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		play1_1Button = new JButton();
		play1_1Button.setAction(playAction);
		panel.add(play1_1Button);
		play1_2Button = new JButton();
		play1_2Button.setAction(playAction);
		panel.add(play1_2Button);
		pause1Button = new JButton();
		pause1Button.setAction(pauseAction);
		panel.add(pause1Button);
		JButton fsButton = new JButton();
		fsButton.setAction(fsAction);
		panel.add(fsButton);
		pos1Button = new JButton();
		pos1Button.setAction(posAction);
		panel.add(pos1Button);
		return panel;
	}

	private Component getButtons2() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		play2_1Button = new JButton();
		play2_1Button.setAction(playAction);
		panel.add(play2_1Button);
		play2_2Button = new JButton();
		play2_2Button.setAction(playAction);
		panel.add(play2_2Button);
		pause2Button = new JButton();
		pause2Button.setAction(pauseAction);
		panel.add(pause2Button);
		tvButton = new JButton();
		tvButton.setAction(tvAction);
		panel.add(tvButton);
		pos2Button = new JButton();
		pos2Button.setAction(posAction);
		panel.add(pos2Button);
		return panel;
	}

	private void init() throws Exception {
		getEmbedded1MPlayerIn();
		getEmbedded2MPlayerIn();
	}
	
	public static void main(String[] args) throws Exception {
		CopyOfTestFrame frm = new CopyOfTestFrame();
		frm.setVisible(true);
		frm.init();
	}

	private Action playAction = new AbstractAction("Play") {

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			String video = src.equals(play1_1Button) || src.equals(play2_1Button) ? VIDEO2 : VIDEO3;
			if (src.equals(play2_1Button) || src.equals(play2_2Button)) {
				terminateProcess(tvMPlayerIn);
			}
			try {
				PrintStream in = (src.equals(play1_1Button) || src.equals(play1_2Button)) ? getEmbedded1MPlayerIn() : getEmbedded2MPlayerIn();
				in.print("loadfile \"" + video + "\" 0\n");
				in.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action pauseAction = new AbstractAction("Pause") {

		public void actionPerformed(ActionEvent e) {
			try {
				Object src = e.getSource(); 
				PrintStream in = src.equals(pause1Button) ? getEmbedded1MPlayerIn() : getEmbedded2MPlayerIn();
				in.print("pause\n");
				in.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action fsAction = new AbstractAction("F-S") {

		public void actionPerformed(ActionEvent e) {
			try {
				terminateProcess(embedded1MPlayerIn);
				terminateProcess(embedded2MPlayerIn);
				terminateProcess(fsMPlayerIn);
				terminateProcess(tvMPlayerIn);
				
				PrintStream fsIn = getFsMPlayerIn();
//				fsIn.print("\nloadfile \"" + VIDEO1 + "\" 0\n");
//				fsIn.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action tvAction = new AbstractAction("TV") {

		public void actionPerformed(ActionEvent e) {
			try {
				terminateProcess(embedded1MPlayerIn);
				terminateProcess(embedded2MPlayerIn);
				terminateProcess(fsMPlayerIn);
				terminateProcess(tvMPlayerIn);
				
				PrintStream tvIn = getTVMPlayerIn();
//				tvIn.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action posAction = new AbstractAction("Pos.") {

		public void actionPerformed(ActionEvent e) {
			try {
				Object src = e.getSource();
				PrintStream in = src.equals(pos1Button) ? getEmbedded1MPlayerIn() : getEmbedded2MPlayerIn();
				in.print("get_property percent_pos\n");
				in.flush();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private PrintStream getEmbedded1MPlayerIn() throws IOException {
		if (embedded1MPlayerIn == null) {
			System.out.println("------Creating player 'embedded1'");
			Process process = Runtime.getRuntime().exec(
					MPLAYER_EXE 
							+ " -wid "
							+ com.sun.jna.Native.getComponentID(canvas1)
							+ " -slave " 
							+ " -idle "
							+ " -vo directx "
							+ " -nokeepaspect "
							+ " -colorkey 0x000000 " 
							+ " foo.avi"
							);
			embedded1MPlayerIn = new PrintStream(process.getOutputStream());
			ProcessExitDetector exitDetector = new ProcessExitDetector(process, "embedded1");
			exitDetector.addProcessListener(this);
			exitDetector.start();
			new StreamLogger("embedded1", process.getInputStream(), Level.INFO).start();
			new StreamLogger("embedded1", process.getInputStream(), Level.WARN).start();
		}
		return embedded1MPlayerIn;
	}

	private PrintStream getEmbedded2MPlayerIn() throws IOException {
		if (embedded2MPlayerIn == null) {
			System.out.println("------Creating player 'embedded2'");
			Process process = Runtime.getRuntime().exec(
					MPLAYER_EXE 
							+ " -wid "
							+ com.sun.jna.Native.getComponentID(canvas2)
							+ " -slave " 
							+ " -idle "
							+ " -vo directx "
							+ " -nokeepaspect "
							+ " -colorkey 0xFFFFFF " 
							+ " foo.avi"
							);
			embedded2MPlayerIn = new PrintStream(process.getOutputStream());
			ProcessExitDetector exitDetector = new ProcessExitDetector(process, "embedded2");
			exitDetector.addProcessListener(this);
			exitDetector.start();
			new StreamLogger("embedded2", process.getInputStream(), Level.INFO).start();
			new StreamLogger("embedded2", process.getInputStream(), Level.WARN).start();
		}
		return embedded2MPlayerIn;
	}

	private PrintStream getFsMPlayerIn() throws IOException {
		if (fsMPlayerIn == null) {
			System.out.println("------Creating player 'fullScreen'");
			Process process = Runtime.getRuntime().exec(
					MPLAYER_EXE 
							+ " -slave " 
							+ " -fs "
//							+ " -idle "
							+ " -vo directx "
//							+ " -nokeepaspect "
//							+ " -colorkey 0x000000 " 
							+ "\"" + VIDEO1 + "\""
							);
			fsMPlayerIn = new PrintStream(process.getOutputStream());
			ProcessExitDetector exitDetector = new ProcessExitDetector(process, "fullScreen");
			exitDetector.addProcessListener(this);
			exitDetector.start();
			new StreamLogger("fullScreen", process.getInputStream(), Level.INFO).start();
			new StreamLogger("fullScreen", process.getInputStream(), Level.WARN).start();
		}
		return fsMPlayerIn;
	}

	private PrintStream getTVMPlayerIn() throws IOException {
		if (tvMPlayerIn == null) {
			System.out.println("------Creating player 'tv'");
			Process process = Runtime.getRuntime().exec(
					MPLAYER_EXE 
							+ " -slave " 
							+ " -wid "
							+ com.sun.jna.Native.getComponentID(canvas2)
							+ " -colorkey 0xFFFFFF " 
//							+ " -fs "
							+ " -idle "
//							+ " -vo directx "
//							+ " tv://" + CHANNEL
							+ " -dvbin file=" + CHANNELS_CONF + " "
							+ " dvb://CINE+(ERT)"
							);
			tvMPlayerIn = new PrintStream(process.getOutputStream());
			ProcessExitDetector exitDetector = new ProcessExitDetector(process, "tv");
			exitDetector.addProcessListener(this);
			exitDetector.start();
			new StreamLogger("tv", process.getInputStream(), Level.INFO).start();
			new StreamLogger("tv", process.getInputStream(), Level.WARN).start();
		}
		return tvMPlayerIn;
	}

	/**
	 * Sends a termination signal to the process and nullifies the stream pointer.
	 */
	private void terminateProcess(PrintStream stream) {
		if (stream != null) {
			stream.print("quit 0\n");
			stream.flush();
			stream = null;
		}
	}
	
	/** 
	 * @see com.kesdip.common.util.process.ProcessExitListener#processFinished(java.lang.Process)
	 */
	@Override
	public void processFinished(Process process, Object userObject) {
		System.out.println("------Player exited: " + userObject);
		if ("embedded1".equals(userObject)) {
			embedded1MPlayerIn = null;
		} else if ("embedded2".equals(userObject)) {
			embedded2MPlayerIn = null; 
		} else if ("fullScreen".equals(userObject)) {
			fsMPlayerIn = null;
			ActionEvent ev = new ActionEvent(play1_1Button, 0, "");
			playAction.actionPerformed(ev);
		} else {
			tvMPlayerIn = null;
			ActionEvent ev = new ActionEvent(play2_1Button, 0, "");
			playAction.actionPerformed(ev);
		}
	}

	
	private class WindowCloseListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				terminateProcess(embedded1MPlayerIn);
				terminateProcess(embedded2MPlayerIn);
				terminateProcess(fsMPlayerIn);
				terminateProcess(tvMPlayerIn);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				System.exit(0);
			}
		}
	}
}
