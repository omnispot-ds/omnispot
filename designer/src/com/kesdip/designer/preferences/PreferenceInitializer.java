package com.kesdip.designer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kesdip.designer.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_VLC_PATH,
				"C:\\Program Files\\VideoLAN\\VLC");
		store.setDefault(PreferenceConstants.P_MPLAYER_FILE,
				"C:\\OmniSpot\\AdminServer\\MPlayer\\mplayer.exe");
	}

}
