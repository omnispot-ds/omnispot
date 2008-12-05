/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.registry;

import java.io.InputStream;

import com.kesdip.player.components.Resource;

/**
 * The interface that the content registry must adhere to.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class ContentRegistry {
	private static ContentRegistry singleton = new ContentRegistryImpl();
	
	/**
	 * Helper to access the "default" content registry.
	 * @return The "default" content registry.
	 */
	public static ContentRegistry getContentRegistry() {
		return singleton;
	}
	
	/**
	 * Given a GUID this function returns true, iff the resource that
	 * corresponds to the GUID is existent locally.
	 * @param resource The resource to look for.
	 * @return True iff the resource that corresponds to the GUID is existent
	 * locally.
	 */
	public abstract boolean hasResource(Resource resource);
	
	/**
	 * Utility function to return a resource as a stream.
	 * @param resource The resource.
	 * @return An InputStream instance for the resource, or null if the
	 * resource is not existent locally.
	 */
	public abstract InputStream getResourceAsStream(Resource resource);
	
	/**
	 * Utility function to return a path to a resource.
	 * @param resource The resource.
	 * @return A path to the resource, or null if the resource is not existent
	 * locally.
	 */
	public abstract String getResourcePath(Resource resource);
}
