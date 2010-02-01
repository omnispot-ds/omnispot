/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 08 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;

import com.kesdip.designer.constenum.IFileNames;

/**
 * Utility methods to handle the versions file. The file name is
 * <code>{workspace}/{@link IFileNames#VERSIONS_TXT}</code> and is assumed to
 * have one plugin version at a line.
 * 
 * @author gerogias
 */
public class VersionsFileUtil {

	/**
	 * Private constructor.
	 */
	private VersionsFileUtil() {
		// do nothing
	}

	/**
	 * Creates the file, writing the given version string. If the file exists,
	 * it is re-written.
	 * 
	 * @param pluginVersion
	 *            the version to write
	 * @throws IOException
	 *             on error
	 */
	public static void createFile(String pluginVersion) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(getVersionsFile(), false);
			writer.write(pluginVersion + "\n");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Appends the version at the end of the file. If the file does not exist,
	 * it is created.
	 * 
	 * @param pluginVersion
	 *            the version to write
	 * @throws IOException
	 *             on error
	 */
	public static void appendVersion(String pluginVersion) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(getVersionsFile(), true);
			writer.write(pluginVersion + "\n");
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * @return boolean <code>false</code> if the file does not exist
	 */
	public static boolean fileExists() {
		return getVersionsFile().isFile();
	}

	/**
	 * @return List a sorted list of the versions contained in the file, from
	 *         oldest to newest. If the file does not exist, an empty list
	 * @throws IOException
	 *             on error
	 */
	public static List<String> getPluginVersions() throws IOException {
		List<String> versions = new ArrayList<String>();
		if (!fileExists()) {
			return versions;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getVersionsFile()));
			String item = null;
			while ((item = reader.readLine()) != null) {
				versions.add(item);
			}
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		// sort the list of versions lexicographically
		Collections.sort(versions);
		return versions;
	}

	/**
	 * Implemented as a getter to make sure the workspace has been initialized.
	 * 
	 * @return File the version file
	 */
	private static File getVersionsFile() {
		Location workspaceLocation = Platform.getInstanceLocation();
		File versionsFile = new File(workspaceLocation.getURL().getFile(),
				IFileNames.VERSIONS_TXT);
		return versionsFile;
	}
}
