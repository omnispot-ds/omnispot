package com.kesdip.player.components;

import java.awt.Canvas;
import java.awt.Dimension;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.videolan.jvlc.internal.LibVlc;
import org.videolan.jvlc.internal.LibVlc.LibVlcInstance;
import org.videolan.jvlc.internal.LibVlc.libvlc_exception_t;

import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.helpers.PlayerUtils;
import com.kesdip.player.registry.ContentRegistry;

/**
 * Represents a component that renders a video (through the VLC client
 * interface).
 * 
 * @author Pafsanias Ftakas
 */
public class Video extends AbstractComponent implements ApplicationContextAware {
	
	/* SPRING STATE */
	private String content;
	
	public void setContent(String content) {
		this.content = content;
	}
	
	/* SPRING CONFIGURATION STATE */
	private ApplicationContext ctx;
	private DeploymentSettings settings;
	
	/* TRANSIENT STATE */
	private LibVlc libVlc;
	private libvlc_exception_t exception;
	private LibVlcInstance libvlc_instance_t;
	private int libVlcItem;
	private Canvas canvas;
	
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

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
        		"--plugin-path=" + settings.getVlcPath() };
        libvlc_instance_t = libVlc.libvlc_new(ma.length,
        		ma, exception);
        assertOnException();
        
        System.out.println("Initialized LibVLC instance");
        
        ContentRegistry registry = ContentRegistry.getContentRegistry();
        String videoFilename = registry.getResourcePath(content);
        
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

	@Override
	public void init(Component parent) throws ComponentException {
		try {
			settings = (DeploymentSettings) ctx.getBean("deploymentSettings");

			initVLC();
			
			canvas = new Canvas();
			canvas.setCursor(PlayerUtils.getNoCursor());
			canvas.setLocation(x, y);
			canvas.setSize(new Dimension(width, height));
			canvas.setPreferredSize(new Dimension(width, height));
			canvas.addKeyListener(PlayerUtils.getExitKeyListener());
			
			parent.add(this);
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

	private boolean firstTime = true;
	
	@Override
	public void repaint() throws ComponentException {
		if (firstTime) {
			try {
				startVideoOnCanvas();
			} catch (Exception e) {
				throw new ComponentException("Unable to start video", e);
			}
			firstTime = false;
		}
	}

}
