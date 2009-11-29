/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import com.kesdip.common.util.StringUtils;

/**
 * Configuration settings for an {@link MPlayer} instance redering DVB-T signal.
 * Unsupported in this version.
 * 
 * @author gerogias
 */
public class DVBTConfiguration extends MPlayerConfiguration {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the channle stream to render.
	 */
	private String streamName = null;

	/**
	 * Full path to the <code>channels.conf</code> file.
	 */
	private String channelsConfFile = null;

	/**
	 * Default constructor.
	 */
	public DVBTConfiguration() {
		setPlayerName("dvb-t");
	}

	/**
	 * @return {@link MPlayerConfiguration} a DVB-T config instance
	 * @see com.kesdip.player.components.media.MPlayerConfiguration#clone()
	 */
	@Override
	public MPlayerConfiguration clone() {
		DVBTConfiguration config = new DVBTConfiguration();
		updateClone(config);
		config.streamName = this.streamName;
		config.channelsConfFile = this.channelsConfFile;
		return config;
	}

	/**
	 * @return the streamName
	 */
	public String getStreamName() {
		return streamName;
	}

	/**
	 * @param streamName
	 *            the streamName to set
	 */
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}

	/**
	 * @return the channelsConfFile
	 */
	public String getChannelsConfFile() {
		return channelsConfFile;
	}

	/**
	 * @param channelsConfFile
	 *            the channelsConfFile to set
	 */
	public void setChannelsConfFile(String channelsConfFile) {
		this.channelsConfFile = channelsConfFile;
	}

	/**
	 * @see com.kesdip.player.components.media.MPlayerConfiguration#isValid()
	 */
	@Override
	public boolean isValid() {
		boolean res = super.isValid();
		if (!res) {
			return false;
		}
		if (StringUtils.isEmpty(streamName)) {
			return false;
		}
		if (StringUtils.isEmpty(channelsConfFile)) {
			return false;
		}
		return true;
	}

}
