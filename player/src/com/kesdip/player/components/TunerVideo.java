package com.kesdip.player.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.constenum.TunerReceptionTypes;

/**
 * Configures the VLC instance to playback analog TV.
 * 
 * @author gerogias
 * @see http://wiki.videolan.org/VLC_command-line_help
 */
public class TunerVideo extends AbstractVideo {
	private static final Logger logger = Logger.getLogger(TunerVideo.class);

	/* SPRING STATE */
	private int type;
	private String videoDevice;
	private String audioDevice;
	private int channel;
	private int input;

	public void setType(int type) {
		if (type != TunerReceptionTypes.ANALOG
				&& type != TunerReceptionTypes.DIGITAL) {
			throw new IllegalArgumentException(
					"The Tuner Video type must be either 1"
							+ "(for analog) or 2 (for digital) reception.");
		}
		this.type = type;
	}

	public void setVideoDevice(String videoDevice) {
		this.videoDevice = videoDevice;
	}

	public void setAudioDevice(String audioDevice) {
		this.audioDevice = audioDevice;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public void setInput(int input) {
		this.input = input;
	}

	/**
	 * Creates a new VLC instance.
	 * 
	 * @see com.kesdip.player.components.AbstractVideo#createVLCInstance(boolean)
	 */
	@Override
	protected void createVLCInstance(boolean fullscreen) throws Exception {
		File pluginsPath = new File(Player.getVlcPath() + File.separator
				+ "plugins");
		List<String> args = new ArrayList<String>();
		// detailed logging
		if (logger.isTraceEnabled()) {
			args.add("-vvv");
		}
		// no annoying video title
		args.add("--no-video-title-show");
		// force aspect ratio so that there are no black borders
		args.add("--aspect-ratio=" + width + ":" + height);
		// no overlays
		args.add("--no-overlay");
		// path to plugins folder
		args.add("--plugin-path=\"" + pluginsPath.getAbsolutePath() + '"');
		// full-screen mode
		args.add(fullscreen ? "--fullscreen" : "--no-fullscreen");
		if (type == TunerReceptionTypes.ANALOG) {
			args.add("dshow://");
			// 200ms content caching
			args.add(":dshow-caching=200");
			// video device name
			args.add(":dshow-vdev=\"" + videoDevice + '"');
			// audio device name
			args.add(":dshow-adev=\"" + audioDevice + '"');
			// tuner channel (UHF, VHF)
			args.add(":dshow-tuner-channel=" + channel);
			// default country
			args.add(":dshow-tuner-country=0");
			// antenna tuner input
			args.add(":dshow-tuner-input=2");
			// h/w-specific video input
			args.add(":show-video-input=" + input);
			// select TV tuner
			args.add(":dshow-amtuner-mode=1");
			
//			args.add("--dshow-chroma=YUY2"); 
//			args.add("--dshow-size=\"100x100\"");
			// suppress config dialog boxes
			args.add(":no-dshow-config");
			args.add(":no-dshow-tuner");
		} else {
			args.add("dvb-t://");
			args.add(":dvb-frequency=" + channel);
			// in Greece it is always 8
			// TODO make editable for other countries
			args.add(":dvb-bandwidth=8");
			args.add("--program=" + input);
		}
		// set instance flag
		this.fullScreen = fullscreen;
		if (logger.isDebugEnabled()) {
			StringBuilder cmd = new StringBuilder();
			for (String item : args) {
				cmd.append(item).append(' ');
			}
			logger.debug("VLC command line: " + cmd);
		}
		// init native component
		String[] ma = args.toArray(new String[args.size()]);
		libvlc_instance_t = libVlc.libvlc_new(ma.length, ma, exception);
		assertOnException("createVLCInstance.libvlc_new");

		logger.trace("Initialized LibVLC instance");
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		super.init(parent, timingMonitor, player);

		try {
			initVLC(isFullScreen());
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}
}
