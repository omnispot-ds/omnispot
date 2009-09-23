/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kesdip.player.components.media.VideoConfiguration.Playlist;

/**
 * @author gerogias
 * 
 */
public class TestFrame extends JFrame implements MPlayerEventListener {

	private Canvas canvas1 = null;
	
	private Canvas canvas2 = null;

	private MPlayer embedded1MPlayer = null;

	private MPlayer embedded2MPlayer = null;

	private MPlayer fsMPlayer = null;
	
	private MPlayer tvMPlayer = null;
	
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

	private final String VIDEO1 = "C:/Documents and Settings/gerogias/Desktop/mail/WhoSaysWomenCan_tPark.wmv";
	
	private final String VIDEO2 = "C:/Documents and Settings/gerogias/Desktop/mail/chicken police (1).mpg";
	
	private final String VIDEO3 = "C:/Documents and Settings/gerogias/Desktop/mail/promenade(ch).wmv";
	
	private final String CHANNEL = "48";
	
	private final String CHANNELS_CONF = "C:/dbin/MPlayer-tv-directx/channels.conf";

	private TestFrame() {
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
		getEmbedded1MPlayer();
		getEmbedded2MPlayer();
	}
	
	public static void main(String[] args) throws Exception {
		TestFrame frm = new TestFrame();
		frm.setVisible(true);
		frm.init();
	}

	private Action playAction = new AbstractAction("Play") {

		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			String video = src.equals(play1_1Button) || src.equals(play2_1Button) ? VIDEO2 : VIDEO3;
			if (src.equals(play2_1Button) || src.equals(play2_2Button)) {
				terminatePlayer(tvMPlayer);
			}
			try {
				MPlayer player = (src.equals(play1_1Button) || src.equals(play1_2Button)) ? getEmbedded1MPlayer() : getEmbedded2MPlayer();
				player.playFile(video, true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action pauseAction = new AbstractAction("Pause") {

		public void actionPerformed(ActionEvent e) {
			try {
				Object src = e.getSource(); 
				MPlayer player = src.equals(pause1Button) ? getEmbedded1MPlayer() : getEmbedded2MPlayer();
				player.pause();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action fsAction = new AbstractAction("F-S") {

		public void actionPerformed(ActionEvent e) {
			try {
				terminatePlayer(embedded1MPlayer);
				terminatePlayer(embedded2MPlayer);
				terminatePlayer(fsMPlayer);
				terminatePlayer(tvMPlayer);
				
				fsMPlayer = getFsMPlayer();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action tvAction = new AbstractAction("TV") {

		public void actionPerformed(ActionEvent e) {
			try {
//				terminatePlayer(embedded1MPlayer);
				terminatePlayer(embedded2MPlayer);
				terminatePlayer(fsMPlayer);
				terminatePlayer(tvMPlayer);
				
				tvMPlayer = getTVMPlayer();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private Action posAction = new AbstractAction("Pos.") {

		public void actionPerformed(ActionEvent e) {
			try {
				Object src = e.getSource();
				MPlayer player = src.equals(pos1Button) ? getEmbedded1MPlayer() : getEmbedded2MPlayer();
				player.pollProgress();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	private MPlayer getEmbedded1MPlayer() throws IOException {
		if (embedded1MPlayer == null) {
			VideoConfiguration config = new VideoConfiguration();
			config.setPlayerName("embedded1");
			config.setColorKey(Color.BLACK);
			config.setWindowId(com.sun.jna.Native.getComponentID(canvas1));
			config.setLoop(true);
			Playlist playlist = new Playlist("test");
			playlist.setFullScreen(false);
			playlist.addFile(VIDEO1);
			playlist.addFile(VIDEO2);
			config.addPlaylist(playlist);
			embedded1MPlayer = MPlayer.getInstance(config);
		}
		return embedded1MPlayer;
	}

	private MPlayer getEmbedded2MPlayer() throws IOException {
		if (embedded2MPlayer == null) {
			VideoConfiguration config = new VideoConfiguration();
			config.setPlayerName("embedded2");
			config.setColorKey(Color.WHITE);
			config.setWindowId(com.sun.jna.Native.getComponentID(canvas2));
			config.setLoop(false);
			embedded2MPlayer = MPlayer.getInstance(config);
//			embedded2MPlayer.addFile(VIDEO2);
//			embedded2MPlayer.play();
		}
		return embedded2MPlayer;
	}

	private MPlayer getFsMPlayer() throws IOException {
		if (fsMPlayer == null) {
			System.out.println("------Creating player 'fullScreen'");
			VideoConfiguration config = new VideoConfiguration();
			config.setPlayerName("fullScreen");
			config.setLoop(false);
			Playlist playlist = new Playlist("test");
			playlist.setFullScreen(true);
			playlist.addFile(VIDEO3);
			config.addPlaylist(playlist);
			fsMPlayer = MPlayer.getInstance(config);
		}
		return fsMPlayer;
	}

	private MPlayer getTVMPlayer() throws IOException {
		if (tvMPlayer == null) {
			System.out.println("------Creating player 'tv'");
			AnalogTVConfiguration config = new AnalogTVConfiguration();
			config.setPlayerName("tv");
			config.setColorKey(Color.WHITE);
			config.setWindowId(com.sun.jna.Native.getComponentID(canvas2));
			config.setFullScreen(false);
			config.setChannel(18);
			tvMPlayer = MPlayer.getInstance(config);
		}
		return tvMPlayer;
	}

	/**
	 * Sends a termination signal to the process and nullifies the stream pointer.
	 */
	private void terminatePlayer(MPlayer player) {
		if (player != null) {
			player.terminate();
			player = null;
		}
	}
	
	@Override
	public void playbackCompleted(String name) {
		System.out.println("------Playback completed: " + name);
//		if ("embedded1".equals(userObject)) {
//			embedded1MPlayerIn = null;
//		} else if ("embedded2".equals(userObject)) {
//			embedded2MPlayerIn = null; 
//		} else if ("fullScreen".equals(userObject)) {
//			fsMPlayerIn = null;
//			ActionEvent ev = new ActionEvent(play1_1Button, 0, "");
//			playAction.actionPerformed(ev);
//		} else {
//			tvMPlayerIn = null;
//			ActionEvent ev = new ActionEvent(play2_1Button, 0, "");
//			playAction.actionPerformed(ev);
//		}
	}

	
	private class WindowCloseListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				terminatePlayer(embedded1MPlayer);
				terminatePlayer(embedded2MPlayer);
				terminatePlayer(fsMPlayer);
				terminatePlayer(tvMPlayer);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				System.exit(0);
			}
		}
	}
}
