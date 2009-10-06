/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 23 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import com.kesdip.common.util.FileUtils;
import com.kesdip.player.Player;
import com.kesdip.player.constenum.TunerReceptionTypes;

/**
 * Displays video from the TV tuner (analog or DVB-T) using {@link MPlayer}.
 * 
 * TODO DVB-T not supported for now
 * TODO Analog TV has no sound support
 * 
 * @author gerogias
 */
public class TunerVideo extends AbstractMPlayerVideo {

	/**
	 * Component type (analog, digital).
	 */
	private int type;

	/**
	 * Analog channel to tune into. Channels may be numbers or strings of type
	 * <code>s40</code>, <code>e7</code>.
	 */
	private String channel;

	/**
	 * The audio device to use. Ignored if -1.
	 */
	private int audioDevice = -1;

	/**
	 * Is the playback fullscreen?
	 */
	private boolean fullScreen;

	/**
	 * @see com.kesdip.player.components.media.AbstractMPlayerVideo#getPlayerConfiguration()
	 */
	@Override
	protected MPlayerConfiguration getPlayerConfiguration() {
		MPlayerConfiguration config = createConfiguration();
		// common settings
		config.setPlayerName(super.id != null ? super.id : "TunerVideo");
		config.setColorKey(getWindowComponent().getBackground());
		config.setFullScreen(fullScreen);
		config.setPlayerExecutable(FileUtils.getNativePathName(Player
				.getMPlayerFile()));
		if (!fullScreen) {
			config.setWindowId(com.sun.jna.Native
					.getComponentID(getWindowComponent()));
		}
		return config;
	}

	/**
	 * @param type
	 *            the component's type
	 */
	public void setType(int type) {
		if (type != TunerReceptionTypes.ANALOG
				&& type != TunerReceptionTypes.DIGITAL) {
			throw new IllegalArgumentException(
					"The Tuner Video type must be either 1"
							+ "(for analog) or 2 (for digital) reception.");
		}
		if (audioDevice < -1) {
			throw new IllegalArgumentException(
					"The audio device cannot be a negative number.");
		}
		this.type = type;
	}

	/**
	 * @param channel
	 *            the analog channel
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @param fullScreen
	 *            the fullScreen to set
	 */
	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	/**
	 * Create the right type of configuration, depending on type.
	 * 
	 * @return {@link MPlayerConfiguration} the instance
	 */
	private final MPlayerConfiguration createConfiguration() {
		MPlayerConfiguration instance = null;
		if (type == TunerReceptionTypes.ANALOG) {
			instance = new AnalogTVConfiguration();
			((AnalogTVConfiguration) instance).setChannel(channel);
			((AnalogTVConfiguration) instance).setAudioDevice(audioDevice);
		} else {
			// TODO Add support for DVB-T
			instance = new DVBTConfiguration();
			((DVBTConfiguration) instance).setChannelsConfFile(null);
			((DVBTConfiguration) instance).setStreamName(null);
		}
		return instance;
	}

	/**
	 * @param audioDevice
	 *            the audioDevice to set
	 */
	public void setAudioDevice(int audioDevice) {
		this.audioDevice = audioDevice;
	}

	// FIXME: the following are ignored, added because designer adds them

	private String videoDevice = null;

	private String input = null;

	/**
	 * @param videoDevice
	 *            the device to set
	 */
	public void setVideoDevice(String videoDevice) {
		this.videoDevice = videoDevice;
	}

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}

}
