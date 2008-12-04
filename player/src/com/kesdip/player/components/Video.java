/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.PlayerUtils;
import com.kesdip.player.registry.ContentRegistry;

/**
 * Represents a component that renders a video (through the VLC client
 * interface).
 * 
 * @author Pafsanias Ftakas
 */
public class Video extends AbstractComponent
		implements ApplicationContextAware, InitializingBean {
	private static final Logger logger = Logger.getLogger(Video.class);
	
	/* SPRING STATE */
	private List<Resource> contents;
	private boolean repeat = false;
	
	public void setContent(List<Resource> content) {
		this.contents = content;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
	
	/* SPRING CONFIGURATION STATE */
	private ApplicationContext ctx;
	private DeploymentSettings settings;
	
	/* TRANSIENT STATE */
	private Class<?> libVlcClass;
	private Class<?> libVlcExceptionClass;
	private Class<?> libVlcInstanceClass;
	private Object libVlc;
	private Object exception;
	private Object libvlc_instance_t;
	private Canvas canvas;
	
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (contents == null || contents.size() == 0)
			throw new Exception("Video component must have a non-empty " +
					"contents property.");
	}

	private void initVLC() throws Exception {
		libVlcClass = Class.forName("org.videolan.jvlc.internal.LibVlc");
        libVlc = libVlcClass.getField("SYNC_INSTANCE").get(null);
        libVlcExceptionClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$libvlc_exception_t");
        libVlcInstanceClass = Class.forName(
        		"org.videolan.jvlc.internal.LibVlc$LibVlcInstance");
        
        logger.info("Starting vlc");
        logger.info("version: " +
        		libVlcClass.getMethod("libvlc_get_version").invoke(libVlc));
        logger.info("changeset: " +
        		libVlcClass.getMethod("libvlc_get_changeset").invoke(libVlc));
        logger.info("compiler: " +
        		libVlcClass.getMethod("libvlc_get_compiler").invoke(libVlc));
        
        loadMedia();
	}
	
	private void assertOnException() throws Exception {
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
	
	private void loadMedia() throws Exception {
        exception = libVlcExceptionClass.newInstance();
        libVlcClass.getMethod("libvlc_exception_init", libVlcExceptionClass).
			invoke(libVlc, exception);

		String[] ma = new String[] {
         		"-vvv",
         		"--no-video-title-show",
        		"--plugin-path=" + settings.getVlcPath() };
		libvlc_instance_t = libVlcClass.
			getMethod("libvlc_new", int.class, String[].class, libVlcExceptionClass).
			invoke(libVlc, ma.length, ma, exception);
        assertOnException();
        
        logger.info("Initialized LibVLC instance");
        
        ContentRegistry registry = ContentRegistry.getContentRegistry();
        for (Resource resource : contents) {
	        String videoFilename = registry.getResourcePath(resource.getIdentifier());
	        
	        libVlcClass.
				getMethod("libvlc_playlist_add", libVlcInstanceClass, String.class, String.class, libVlcExceptionClass).
				invoke(libVlc, libvlc_instance_t, videoFilename, null, exception);
	        assertOnException();
	        
	        logger.info("Loaded video");
        }

        if (repeat) {
        	libVlcClass.
				getMethod("libvlc_playlist_loop", libVlcInstanceClass, int.class, libVlcExceptionClass).
				invoke(libVlc, libvlc_instance_t, 1, exception);
	        assertOnException();
	        
	        System.out.println("Set loop variable");
        }
	}
	
	private void startVideoOnCanvas() throws Exception {
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
			canvas.setBackground(Color.BLACK);
			
			parent.add(this);
			
			firstTime = true;
			stillStarting = true;
			completed = new AtomicBoolean(false);
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

	private boolean firstTime;
	private boolean stillStarting;
	private AtomicBoolean completed;
	
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
		
		if (!repeat) {
			try {
		    	int v = ((Integer) libVlcClass.
					getMethod("libvlc_playlist_isplaying", libVlcInstanceClass, libVlcExceptionClass).
					invoke(libVlc, libvlc_instance_t, exception)).intValue();
		        assertOnException();
		        if (stillStarting) {
		        	if (v == 1) {
		        		stillStarting = false;
		        	}
		        } else {
		        	completed.set(v == 0);
		        }
				
				if (completed.get())
					logger.info("The video component has completed.");
			} catch (Exception e) {
				throw new ComponentException("Unable to query playlist status", e);
			}
		}
	}

	@Override
	public CompletionStatus isComplete() {
		if (repeat)
			return super.isComplete();
		
		if (completed != null && completed.get())
			return CompletionStatus.COMPLETE;
		else
			return CompletionStatus.INCOMPLETE;
	}

	@Override
	public void releaseResources() {
		if (libVlcInstanceClass == null)
			return;
		
		try {
			libVlcClass.
				getMethod("libvlc_release", libVlcInstanceClass).
				invoke(libVlc, libvlc_instance_t);
			assertOnException();
		} catch (Exception e) {
			logger.error("Unable to release resources. Possible memory leak.", e);
		}
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.addAll(contents);
		return retVal;
	}


}
