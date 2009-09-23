/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 23 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import com.kesdip.player.constenum.TunerReceptionTypes;

/**
 * Displays video from the TV tuner (analog or DVB-T) using {@link MPlayer}.
 * 
 * TODO DVB-T not supported for now
 * 
 * @author gerogias
 */
public class TunerVideo extends AbstractMPlayerVideo {

	/**
	 * Component type (analog, digital).
	 */
	private int type;

	/**
	 * Analog channel to tune into.
	 */
	private int channel;

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
		config.setPlayerName(super.id != null ? super.id : "FileVideo");
		config.setColorKey(getWindowComponent().getBackground());
		config.setFullScreen(fullScreen);
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
		this.type = type;
	}

	/**
	 * @param channel
	 *            the analog channel
	 */
	public void setChannel(int channel) {
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
			((AnalogTVConfiguration)instance).setChannel(channel);
		} else {
			// TODO Add support for DVB-T
			instance = new DVBTConfiguration();
			((DVBTConfiguration)instance).setChannelsConfFile(null);
			((DVBTConfiguration)instance).setStreamName(null);
		}
		return instance;
	}
}
