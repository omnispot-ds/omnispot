/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;


/**
 * Configuration for an analog TV {@link MPlayer} instance.
 * 
 * @author gerogias
 */
public class AnalogTVConfiguration extends MPlayerConfiguration {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The channel number to show (1-12 for VHF, 21-99 for UHF).
	 */
	private int channel = 0;

	/**
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	
	/**
	 * Default constructor.
	 */
	public AnalogTVConfiguration() {
		setPlayerName("tv");
	}



	/**
	 * @param channel the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * @return {@link MPlayerConfiguration} a TV config instance
	 * @see com.kesdip.player.components.media.MPlayerConfiguration#clone()
	 */
	@Override
	public MPlayerConfiguration clone() {
		AnalogTVConfiguration config = new AnalogTVConfiguration();
		updateClone(config);
		config.channel = this.channel;
		return config;
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
		if (channel < 1) {
			return false;
		}
		return true;
	}
	
}
