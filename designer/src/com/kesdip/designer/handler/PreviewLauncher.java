package com.kesdip.designer.handler;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

import com.kesdip.designer.utils.DesignerLog;

public class PreviewLauncher {
	
	public static void launchPreview(String deploymentLocation, String vlcPath) {
		try {
			ILaunchConfiguration launchConfiguration = createLaunchConfiguration(deploymentLocation, vlcPath);
			launchConfiguration.launch("run", null);
		} catch (Exception e) {
			DesignerLog.logError("Unable to start deployment preview", e);
		}
	}
	
	private static ILaunchConfiguration createLaunchConfiguration(String deploymentLocation, String vlcPath) throws CoreException, IOException, IllegalStateException {
		ILaunchConfigurationType lcType = getLaunchManager().getLaunchConfigurationType("org.eclipse.ui.externaltools.ProgramLaunchConfigurationType");
		
		String envPlayerLib = System.getenv("KESDIP_PLAYER_LIB");
		String envJavaHome = System.getenv("JAVA_HOME");
		
		if (envPlayerLib == null || envJavaHome == null) {
			throw new IllegalStateException("KESDIP_PLAYER_LIB and/or JAVA_HOME environment variables have not been set");
		}
		
		String name = "Preview Player";
		String cmdLine = new File(envJavaHome + "/bin/java.exe").getCanonicalPath();
		
		StringBuilder classpath = new StringBuilder(envPlayerLib);
		classpath.append(';');
		File fPlayerLib = new File(envPlayerLib);
		File[] jars = fPlayerLib.listFiles();
		for (int i = 0 ; i < jars.length ; i++) {
			classpath.append(jars[i].getCanonicalPath());
			classpath.append(';');
		}
		
		String args = "-cp \"" + classpath.substring(0, classpath.length()-1) + "\" com.kesdip.player.preview.PlayerPreview \"" + deploymentLocation + "\" \"" + vlcPath + "\"";

		ILaunchConfigurationWorkingCopy wc = lcType.newInstance(null, name);
		wc.setAttribute(IExternalToolConstants.ATTR_LOCATION, cmdLine);
		wc.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, args);
		
		DesignerLog.logInfo("Launch configuration created with command line:" + cmdLine + " " + args);
		
		return wc.doSave();
	}
	
	protected static ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

}
