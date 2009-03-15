package com.kesdip.player.components;

import java.io.File;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;

public class TunerVideo extends AbstractVideo {
	private static final Logger logger = Logger.getLogger(TunerVideo.class);
	
	/* SPRING STATE */
	private int type;
	private String device;
	private int channel;
	private int input;
	
	public void setType(int type) {
		if (type != 1 && type != 2)
			throw new IllegalArgumentException("The Tuner Video type must be either 1" +
					"(for analog) or 2 (for digital) reception.");
		this.type = type;
	}
	
	public void setDevice(String device) {
		this.device = device;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public void setInput(int input) {
		this.input = input;
	}
	
	@Override
	protected void createVLCInstance() throws Exception {
        File pluginsPath = new File(Player.getVlcPath() + File.separator + "plugins");
		String[] ma;
		if (type == 1) {
			if (logger.isTraceEnabled())
				ma = new String[] {
						"-vvv",
		         		"--no-video-title-show",
		         		"--no-overlay",
		        		"--plugin-path=" + pluginsPath.getAbsolutePath(),
		        		"dshow://",
		        		":dshow-vdev=" + device,
		        		":dshow-caching=200",
		        		":dshow-tuner-channel=" + channel,
		        		":dshow-tuner-input=2", // Antenna
		        		":dshow-video-input=" + input,
		        		":dshow-amtuner-mode=1" // TV
		        		};
			else
				ma = new String[] {
		         		"--no-video-title-show",
		         		"--no-overlay",
		        		"--plugin-path=" + pluginsPath.getAbsolutePath(),
						"dshow://",
						":dshow-vdev=" + device,
						":dshow-caching=200",
						":dshow-tuner-channel=" + channel,
						":dshow-tuner-input=2", // Antenna
						":dshow-video-input=" + input,
						":dshow-amtuner-mode=1" // TV
						};
		} else {
			if (logger.isTraceEnabled())
				ma = new String[] {
						"-vvv",
		         		"--no-video-title-show",
		         		"--no-overlay",
		        		"--plugin-path=" + pluginsPath.getAbsolutePath(),
		        		"dvb-t://",
		        		":dvb-frequency=" + channel
		        		};
			else
				ma = new String[] {
		         		"--no-video-title-show",
		         		"--no-overlay",
		        		"--plugin-path=" + pluginsPath.getAbsolutePath(),
						"dvb-t://",
						":dvb-frequency=" + channel
						};
		}
		libvlc_instance_t = libVlcClass.
			getMethod("libvlc_new", int.class, String[].class, libVlcExceptionClass).
			invoke(libVlc, ma.length, ma, exception);
        assertOnException();
        
        logger.info("Initialized LibVLC instance");
	}
	
	@Override
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		super.init(parent, timingMonitor, player);
		
		try {
			initVLC();
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}
}
