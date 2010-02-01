/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 07 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.update;

import java.io.IOException;
import java.util.List;

import com.kesdip.designer.constenum.IFileNames;
import com.kesdip.designer.utils.DesignerLog;

/**
 * Takes a plugin version string and checks if it has been updated since the
 * last time it was checked.
 * <p>
 * The plugin is considered updated if the {@link IFileNames#VERSIONS_TXT} file
 * contains only older entries for the plugin. 
 * </p>
 * 
 * @author gerogias
 */
public class VersionUpdateChecker {

	/**
	 * The version to check.
	 */
	private String version = null;

	/**
	 * The plugin to check for.
	 */
	private String pluginId = null;

	/**
	 * The complete string.
	 */
	private String pluginVersion = null;

	/**
	 * The previously installed version number.
	 */
	private String previousVersion = null;

	/**
	 * Default constructor.
	 * 
	 * @param pluginId
	 * @param version
	 */
	public VersionUpdateChecker(String pluginId, String version) {
		this.pluginId = pluginId;
		this.version = version;
		pluginVersion = pluginId + "_" + version;
	}

	/**
	 * Checks if the Designer plugin has been updated since the last run.
	 * 
	 * @return boolean <code>true</code> if it has been updated
	 */
	public boolean isVersionUpdated() {
		// does not exist, create, add entry and return
		if (!VersionsFileUtil.fileExists()) {
			try {
				VersionsFileUtil.createFile(pluginVersion);
			} catch (IOException e) {
				DesignerLog.logError("Error creating "
						+ IFileNames.VERSIONS_TXT, e);
			}
			return false;
		}
		if (!containsOlderVersions()) {
			return false;
		}
		return true;
	}

	/**
	 * Read the version file and see if it contains only older versions for this
	 * plugin.
	 * 
	 * @return boolean <code>true</code> if the file does not contain a plugin
	 *         with a version equal or higher
	 */
	private final boolean containsOlderVersions() {
		boolean foundNewerOrEqualVersion = false;
		try {
			List<String> versions = VersionsFileUtil.getPluginVersions();
			for (String item : versions) {
				if (item.startsWith(pluginId)
						&& pluginVersion.compareTo(item) <= 0) {
					// is a newer or equal version in the file
					foundNewerOrEqualVersion = true;
					break;
				} else if (item.startsWith(pluginId)
						&& pluginVersion.compareTo(item) > 0) {
					// update the previous version string
					previousVersion = item.substring(pluginId.length() + 1);
				}
			}
		} catch (IOException e) {
			DesignerLog.logError("Error reading " + IFileNames.VERSIONS_TXT, e);
		}
		return !foundNewerOrEqualVersion;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the pluginId
	 */
	public String getPluginId() {
		return pluginId;
	}

	/**
	 * @return the pluginVersion
	 */
	public String getPluginVersion() {
		return pluginVersion;
	}

	/**
	 * The value is only populated after a call to
	 * {@link #containsOnlyOlderVersions()}.
	 * 
	 * @return the previousVersion
	 */
	public String getPreviousVersion() {
		return previousVersion;
	}
}
