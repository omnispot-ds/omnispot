/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.registry;

import java.io.InputStream;

/**
 * TODO This is a stub. Implement properly.
 * 
 * @author Pafsanias Ftakas
 */
public class ContentRegistryImpl extends ContentRegistry {

	@Override
	public InputStream getResourceAsStream(String guid) {
		return null;
	}

	@Override
	public String getResourcePath(String guid) {
		return "ad.mp4";
	}

	@Override
	public boolean hasResource(String guid) {
		return false;
	}

}
