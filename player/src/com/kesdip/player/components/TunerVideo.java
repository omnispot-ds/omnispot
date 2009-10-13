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
		if (type == TunerReceptionTypes.ANALOG) {
			args.add("dshow://");
			// 200ms content caching + opening quote
			args.add("\":dshow-caching=200");
			// video device name
			args.add(":dshow-vdev=" + videoDevice);
			// audio device name
			args.add(":dshow-adev=" + audioDevice);
			// tuner channel (UHF, VHF)
			args.add(":dshow-tuner-channel=" + channel);
			// country code (1 or 2 digits as calling code: 30=greece, 0=default)
			args.add(":dshow-tuner-country=30");
			// antenna tuner input (0=default, 1=cable, 2=antenna)
			args.add(":dshow-tuner-input=2");
			// h/w-specific video input
			args.add(":dshow-video-input=" + input);
			// select TV tuner (0=Default, 1=TV, 2=FM radio, 
			// 4=AM radio, 8=DSS)
			args.add(":dshow-amtuner-mode=1");
			// suppress config dialog boxes + closing quote
			args.add(":no-dshow-config");
			args.add(":no-dshow-tuner\"");
		} else {
			args.add("dvb-t://");
			args.add(":dvb-frequency=" + channel);
			// in Greece it is always 8
			// TODO make editable for other countries
			args.add(":dvb-bandwidth=8");
			args.add("--program=" + input);
		}
		// detailed logging
		if (logger.isTraceEnabled()) {
			args.add("--verbose=2");
		}
		// no annoying video title
		args.add("--no-video-title-show");
		// force aspect ratio so that there are no black borders
		// only when not fullscreen
		if (!fullscreen) {
			args.add("--aspect-ratio=" + width + ":" + height);
		}
		// full-screen mode
		if (fullscreen) {
			args.add("--fullscreen");
		} else {
			// no overlays (no native accel.)
			args.add("--no-overlay");
		}
		// path to plugins folder
		args.add("--plugin-path=" + pluginsPath.getAbsolutePath());
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
