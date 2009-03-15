package com.kesdip.player.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.PlayerUtils;

public abstract class AbstractVideo extends AbstractComponent {
	private static final Logger logger = Logger.getLogger(AbstractVideo.class);
	
	/* TRANSIENT STATE */
	protected Class<?> libVlcClass;
	protected Class<?> libVlcExceptionClass;
	protected Class<?> libVlcInstanceClass;
	protected Class<?> libVlcLogClass;
	protected Class<?> libVlcLogIteratorClass;
	protected Class<?> libVlcLogMessageClass;
	protected Object libVlc;
	protected Object exception;
	protected Object libvlc_instance_t;
	protected Object libvlc_log;
	protected Canvas canvas;
	
	protected void initVLC() throws Exception {
		libVlcClass = Class.forName("org.videolan.jvlc.internal.LibVlc");
        libVlc = libVlcClass.getField("SYNC_INSTANCE").get(null);
        libVlcExceptionClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$libvlc_exception_t");
        libVlcInstanceClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$LibVlcInstance");
        libVlcLogClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$LibVlcLog");
        libVlcLogIteratorClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$LibVlcLogIterator");
        libVlcLogMessageClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$libvlc_log_message_t");
        
        logger.info("Starting vlc");
        logger.info("version: " +
        		libVlcClass.getMethod("libvlc_get_version").invoke(libVlc));
        logger.info("changeset: " +
        		libVlcClass.getMethod("libvlc_get_changeset").invoke(libVlc));
        logger.info("compiler: " +
        		libVlcClass.getMethod("libvlc_get_compiler").invoke(libVlc));
        
        exception = libVlcExceptionClass.newInstance();
        libVlcClass.getMethod("libvlc_exception_init", libVlcExceptionClass).
			invoke(libVlc, exception);
        assertOnException();
        
        createVLCInstance();
        
        libvlc_log = libVlcClass.
        	getMethod("libvlc_log_open", libVlcInstanceClass, libVlcExceptionClass).
        	invoke(libVlc, libvlc_instance_t, exception);
        assertOnException();
	}
	
	protected void createVLCInstance() throws Exception {
        File pluginsPath = new File(Player.getVlcPath() + File.separator + "plugins");
		String[] ma;
		if (logger.isTraceEnabled())
			ma = new String[] {
					"-vvv",
	         		"--no-video-title-show",
	         		"--no-overlay",
	        		"--plugin-path=" + pluginsPath.getAbsolutePath() };
		else
			ma = new String[] {
	         		"--no-video-title-show",
	         		"--no-overlay",
	        		"--plugin-path=" + pluginsPath.getAbsolutePath() };
		libvlc_instance_t = libVlcClass.
			getMethod("libvlc_new", int.class, String[].class, libVlcExceptionClass).
			invoke(libVlc, ma.length, ma, exception);
        assertOnException();
        
        logger.info("Initialized LibVLC instance");
	}
	
	protected void assertOnException() throws Exception {
		Integer status = (Integer) libVlcClass.
			getMethod("libvlc_exception_raised", libVlcExceptionClass).
			invoke(libVlc, exception);
		if (status.intValue() == 1) {
			String msg = (String) libVlcClass.
				getMethod("libvlc_exception_get_message", libVlcExceptionClass).
				invoke(libVlc, exception);
			throw new Exception(msg);
		}
	}
	
	protected void startVideoOnCanvas() throws Exception {
        int drawable = (int) com.sun.jna.Native.getComponentID(canvas);
        
        logger.info("Drawable retrieved from underlying window (" + drawable + ")");

    	libVlcClass.
			getMethod("libvlc_video_set_parent", libVlcInstanceClass, long.class, libVlcExceptionClass).
			invoke(libVlc, libvlc_instance_t, drawable, exception);
        assertOnException();
        
        logger.info("Attached the player to the drawable");

    	libVlcClass.
			getMethod("libvlc_playlist_play", libVlcInstanceClass, int.class, int.class, String[].class, libVlcExceptionClass).
			invoke(libVlc, libvlc_instance_t, -1, 0, null, exception);
        assertOnException();
        
        logger.info("Started media playing");
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		setPlayer(player);
		
		try {
			canvas = new Canvas();
			canvas.setCursor(PlayerUtils.getNoCursor());
			canvas.setLocation(x, y);
			canvas.setSize(new Dimension(width, height));
			canvas.setPreferredSize(new Dimension(width, height));
			canvas.addKeyListener(PlayerUtils.getExitKeyListener(player));
			canvas.setBackground(Color.BLACK);
			
			parent.add(this);
			
			firstTime = true;
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}
	
	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("Video component is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return canvas;
	}

	protected boolean firstTime;
	protected int countRepaints;
	
	@Override
	public void repaint() throws ComponentException {
		if (firstTime) {
			countRepaints = 1;
			try {
				startVideoOnCanvas();
			} catch (Exception e) {
				throw new ComponentException("Unable to start video", e);
			}
			firstTime = false;
		}
		
		if (countRepaints++ % 30 != 0)
			return;
		
		countRepaints = 1;
		logger.trace("Starting VLC log dump");
		try {
			Object libvlc_log_iterator = libVlcClass.
				getMethod("libvlc_log_get_iterator", libVlcLogClass, libVlcExceptionClass).
				invoke(libVlc, libvlc_log, exception);
			assertOnException();
			try {
				while (true) {
					int hasNext = (Integer) libVlcClass.
						getMethod("libvlc_log_iterator_has_next",
								libVlcLogIteratorClass, libVlcExceptionClass).
							invoke(libVlc, libvlc_log_iterator, exception);
					assertOnException();
					if (hasNext == 0)
						break;
					Object message = libVlcLogMessageClass.newInstance();
					libVlcClass.getMethod("libvlc_log_iterator_next",
							libVlcLogIteratorClass, libVlcLogMessageClass, libVlcExceptionClass).
						invoke(libVlc, libvlc_log_iterator, message, exception);
					assertOnException();
					String msg = (String) libVlcLogMessageClass.
							getField("psz_message").get(message);
					int severity = (Integer) libVlcLogMessageClass.
							getField("i_severity").get(message);
					switch (severity) {
					case 0:
						logger.info(msg);
						break;
					case 1:
						logger.error(msg);
						break;
					case 2:
						logger.warn(msg);
						break;
					case 3:
						logger.debug(msg);
						break;
					default:
						logger.info(msg + " (Unexected severity: " + severity + ")");
					}
				}
				libVlcClass.getMethod("libvlc_log_clear",
						libVlcLogClass, libVlcExceptionClass).
					invoke(libVlc, libvlc_log, exception);
				assertOnException();
			} finally {
				if (libvlc_log_iterator != null) {
					libVlcClass.getMethod("libvlc_log_iterator_free",
							libVlcLogIteratorClass, libVlcExceptionClass).
						invoke(libVlc, libvlc_log_iterator, exception);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to gather VLC logs", e);
		}
		logger.trace("Completed VLC log dump");
	}

	@Override
	public CompletionStatus isComplete() {
		return super.isComplete();
	}

	@Override
	public void releaseResources() {
		if (libVlcInstanceClass == null)
			return;
		
		try {
			libVlcClass.
				getMethod("libvlc_log_close", libVlcLogClass, libVlcExceptionClass).
				invoke(libVlc, libvlc_log, exception);
			assertOnException();
			libVlcClass.
				getMethod("libvlc_release", libVlcInstanceClass).
				invoke(libVlc, libvlc_instance_t);
		} catch (Exception e) {
			logger.error("Unable to release resources. Possible memory leak.", e);
		}
	}

}
