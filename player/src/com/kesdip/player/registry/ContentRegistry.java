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
	 * 
	 * @return The "default" content registry.
	 */
	public static ContentRegistry getContentRegistry() {
		return singleton;
	}

	/**
	 * Given a GUID this function returns true, iff the resource that
	 * corresponds to the GUID is existent locally.
	 * 
	 * @param resource
	 *            The resource to look for.
	 * @return True iff the resource that corresponds to the GUID is existent
	 *         locally.
	 */
	public abstract boolean hasResource(Resource resource);

	/**
	 * Utility function to return a resource as a stream.
	 * 
	 * @param resource
	 *            The resource.
	 * @return An InputStream instance for the resource, or null if the resource
	 *         is not existent locally.
	 */
	public abstract InputStream getResourceAsStream(Resource resource);

	/**
	 * Utility function to return a path to a resource. Same as calling
	 * {@link #getResourcePath(Resource, boolean)} with a <code>false</code>
	 * flag.
	 * 
	 * @param resource
	 *            The resource.
	 * @return A path to the resource, or null if the resource is not existent
	 *         locally.
	 */
	public abstract String getResourcePath(Resource resource);

	/**
	 * Returns a resource from the local repository. The fallback flag
	 * determines what will be returned if the resource is not located in the
	 * repository.
	 * 
	 * @param resource
	 *            the resource to look for
	 * @param fallback
	 *            if <code>true</code> and the resource is not found, the
	 *            resource's identifier will be returned. If <code>false</code>
	 *            and the resource is not found, <code>null</code> will be
	 *            returned
	 * @return String the translated path to the resource, the resource's
	 *         identifier or <code>null</code>, depending on the resource's
	 *         availability and the value of fallback
	 */
	public abstract String getResourcePath(Resource resource, boolean fallback);
}
