package com.kesdip.player;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.videolan.jvlc.internal.LibVlc;
import org.videolan.jvlc.internal.LibVlc.LibVlcInstance;
import org.videolan.jvlc.internal.LibVlc.libvlc_exception_t;

import com.sun.jna.examples.WindowUtils;

public class Player implements Runnable, KeyListener {
	private static final Logger logger = Logger.getLogger(Player.class);
	
	private String videoFilename = "ad.mp4";
	private String vlcPath = "c:\\Program Files\\VideoLAN\\VLC";
	
	private int X_SIZE = 1280;
	private int Y_SIZE = 800;
	
	private boolean done = false;
	
	private synchronized boolean isDone() {
		return done;
	}
	
	public synchronized void setDone() {
		done = true;
	}
	
	private JFrame videoFrame;
	private Canvas canvas;
	private JFrame tickerFrame;
	private TickerPanel tickerPanel;
	
	private LibVlc libVlc;
	private libvlc_exception_t exception;
	private LibVlcInstance libvlc_instance_t;
	private int libVlcItem;
	
	private void initVLC() throws Exception {
        libVlc = LibVlc.SYNC_INSTANCE;

        System.out.println("Starting vlc");
        System.out.println("version: " + libVlc.libvlc_get_version());
        System.out.println("changeset: " + libVlc.libvlc_get_changeset());
        System.out.println("compiler: " + libVlc.libvlc_get_compiler());
        
        loadMedia();
	}
	
	private void assertOnException() throws Exception {
		if (libVlc.libvlc_exception_raised(exception) == 1) {
			throw new Exception(libVlc.libvlc_exception_get_message(exception));
		}
	}
	
	private void loadMedia() throws Exception {
        exception = new libvlc_exception_t();
        libVlc.libvlc_exception_init(exception);

		String[] ma = new String[] {
         		"-vvv",
         		"--no-video-title-show",
        		"--plugin-path=" + vlcPath };
        libvlc_instance_t = libVlc.libvlc_new(ma.length,
        		ma, exception);
        assertOnException();
        
        System.out.println("Initialized LibVLC instance");
        
        libVlcItem = libVlc.libvlc_playlist_add(libvlc_instance_t, videoFilename, null, exception);
        assertOnException();
        
        System.out.println("Loaded video");

        libVlc.libvlc_playlist_loop(libvlc_instance_t, 1, exception);
        assertOnException();
        
        System.out.println("Set loop variable");
	}
	
	private void startVideoOnCanvas() throws Exception {
        int drawable = (int) com.sun.jna.Native.getComponentID(canvas);
        
        System.out.println("Drawable retrieved from underlying window (" + drawable + ")");

        libVlc.libvlc_video_set_parent(libvlc_instance_t, drawable, exception);
        assertOnException();
        
        System.out.println("Attached the player to the drawable");

        libVlc.libvlc_playlist_play(libvlc_instance_t, libVlcItem, 0, null, exception);
        assertOnException();
        
        System.out.println("Started media playing");
	}

	private void makeVideoFrame() throws Exception {
		initVLC();
		
		videoFrame = new JFrame();
		videoFrame.setUndecorated(true);
		videoFrame.setIgnoreRepaint(true);
		videoFrame.setResizable(false);
		videoFrame.addKeyListener(this);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.createImage(new byte[]{0});
		Cursor noCursor = toolkit.createCustomCursor(image, new Point(1,1),
		                                             "blank cursor");
		videoFrame.setCursor(noCursor);
		
		canvas = new Canvas();
		canvas.setCursor(noCursor);
		videoFrame.add(canvas);
		
		videoFrame.setLocation(0, 0);
		canvas.setPreferredSize(new Dimension(X_SIZE, Y_SIZE));
		canvas.addKeyListener(this);
		
        videoFrame.pack();
        videoFrame.setVisible(true);
        
        startVideoOnCanvas();

        videoFrame.requestFocus();
	}
	
	private void makeTickerFrame() {
		tickerFrame = new JFrame();
		tickerFrame.setUndecorated(true);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.createImage(new byte[]{0});
		Cursor noCursor = toolkit.createCustomCursor(image, new Point(1,1),
		                                             "blank cursor");
		tickerFrame.setCursor(noCursor);
		
		// The following line controls if the frame will be transparent or not.
		// Comment out for default Swing behavior.
		WindowUtils.setWindowTransparent(tickerFrame, true);
		
		tickerFrame.setLocation(0, Y_SIZE - 80);

		tickerPanel = new TickerPanel(new Font("Arial", Font.BOLD, 36),
				Color.WHITE, 2,
				new StringTickerSource("This is a test, I say, old chap! "),
				X_SIZE, 80);
		tickerPanel.addKeyListener(this);
		tickerFrame.add(tickerPanel);
		
		tickerFrame.pack();
		tickerFrame.setVisible(true);
		tickerFrame.requestFocus();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// Intentionally left empty.
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(-1);
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// Intentionally left empty.
	}
	
	@Override
	public void run() {
		try {
			makeVideoFrame();
			makeTickerFrame();
		} catch (Exception e) {
			logger.error("Unable to create window", e);
			return;
		}
		
		while (!isDone()) {
			tickerPanel.repaint();
			try {
				Thread.sleep(30);
			} catch (InterruptedException ie) { }
		}
	}

	public static void main(String[] args) {
		try {
			System.setProperty("sun.java2d.noddraw", "true");
			Player player = new Player();
			new Thread(player, "player").start();
		} catch (Exception e) {
			logger.error("Unable to start player.", e);
		}
	}

}
