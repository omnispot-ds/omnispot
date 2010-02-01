package com.kesdip.designer.utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.kesdip.designer.Activator;

public class DesignerLog {
	public static void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}
	
	public static void logError(String message) {
		log(IStatus.ERROR, IStatus.OK, message, null);
	}

	public static void logError(Throwable throwable) {
		logError("Unexpected Exception", throwable);
	}
	
	public static void logError(String message, Throwable throwable) {
		log(IStatus.ERROR, IStatus.OK, message, throwable);
	}
	
	public static void log(int severity, int code, String message, Throwable throwable) {
		log(createStatus(severity, code, message, throwable));
	}
	
	public static IStatus createStatus(int severity, int code, String message, Throwable throwable) {
		return new Status(severity, Activator.PLUGIN_ID, code, message, throwable);
	}
	
	public static void log(IStatus status) {
		Activator.getDefault().getLog().log(status);
	}
}
