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
import java.io.InputStream;
import java.util.MissingResourceException;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.osgi.util.tracker.ServiceTracker;

import com.kesdip.common.util.StringUtils;
import com.kesdip.common.util.tomcat.ApplicationInfo;
import com.kesdip.common.util.tomcat.ManagerWrapper;
import com.kesdip.common.util.tomcat.ProxyInfo;
import com.kesdip.common.util.tomcat.TomcatManagerInfo;
import com.kesdip.designer.Activator;
import com.kesdip.designer.constenum.IFileNames;
import com.kesdip.designer.utils.DesignerLog;

/**
 * An asynchronous job to remotely update the AdminConsole.
 * <p>
 * The job does the following:
 * <ul>
 * <li>loads the Tomcat Manager app's location and credentials</li>
 * <li>loads the proxy credentials (if any)</li>
 * <li>loads the WAR as a resource</li>
 * <li>contacts the Manager to undeploy the admin-console</li>
 * <li>contacts the Manager to deploy the new WAR</li>
 * </ul>
 * </p>
 * 
 * @author gerogias
 * @see http://blog.eitchnet.ch/?p=46
 * @see http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DialogExamples.htm
 */
public class AdminConsoleUpdateJob implements IRunnableWithProgress {

	/**
	 * Action to trigger when completed.
	 */
	private AdminConsoleUpdateCompletionAction completionAction;

	/**
	 * Wrapper for the application info.
	 */
	private ApplicationInfo appInfo = null;

	/**
	 * The admin console web context path.
	 */
	private static final String ADMIN_CONSOLE_PATH = "/admin-console";

	/**
	 * The new version.
	 */
	private String newVersion = null;

	/**
	 * Default constructor.
	 * 
	 * @param completedAction
	 *            the action to call upon completion
	 * @param newVersion
	 *            the new version string
	 * @param previousVersion
	 *            the previous version string
	 */
	public AdminConsoleUpdateJob(
			AdminConsoleUpdateCompletionAction completedAction,
			String newVersion, String previousVersion) {
		this.completionAction = completedAction;
		appInfo = new ApplicationInfo(ADMIN_CONSOLE_PATH, newVersion,
				previousVersion);
		this.newVersion = newVersion;
	}

	public void run(IProgressMonitor monitor) {
		// activate the progress bar with 5 tasks
		// load server location, load proxy config, load WAR, undeploy, deploy
		monitor.beginTask("Updating AdminConsole", 6);
		// perform the job
		InputStream warStream = null;
		try {
			// Manager info
			monitor.subTask("Loading server location");
			TomcatManagerInfo mgrInfo = getManagerInfo();
			delay(1000);
			monitor.worked(1);
			// proxy info
			monitor.subTask("Loading proxy information");
			ProxyInfo proxyInfo = getProxyInfo(mgrInfo);
			delay(1000);
			monitor.worked(2);
			// load WAR
			monitor.subTask("Loading new server application");
			warStream = getWarStream();
			delay(1000);
			monitor.worked(3);
			// undeploy
			monitor.subTask("Undeploying previous server application");
			monitor.worked(4);
			undeployAdminConsole(mgrInfo, proxyInfo);
			delay(1000);
			// deploy
			monitor.subTask("Deploying new server application");
			monitor.worked(5);
			deployAdminConsole(mgrInfo, proxyInfo, warStream);
			// set the completion task to be ok
			completionAction.setOk(true);
			// update the versions file
			VersionsFileUtil.appendVersion(Activator.PLUGIN_ID + '_'
					+ newVersion);
		} catch (Exception e) {
			DesignerLog.logError(e);
			// if the work failed then set the completion
			// task to be NOT ok and set the exception so it
			// can be shown to the user
			completionAction.setThrowable(e);
			completionAction.setOk(false);
		} finally {
			try {
				warStream.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		// stop the monitor
		monitor.done();
		// execute the completion task
		showResults(completionAction);
	}

	/**
	 * Undeploy the admin-console application.
	 * 
	 * @param mgrInfo
	 *            the tomcat manager app
	 * @param proxyInfo
	 *            the proxy info (may be <code>null</code>)
	 */
	private void undeployAdminConsole(TomcatManagerInfo mgrInfo,
			ProxyInfo proxyInfo) {

		ManagerWrapper wrapper = null;
		try {
			wrapper = new ManagerWrapper(mgrInfo, appInfo, proxyInfo);
			wrapper.undeployApp();
			DesignerLog.logInfo("Undeployed previous version: "
					+ wrapper.getUndeployPath());
		} catch (Exception e) {
			DesignerLog.logInfo("Error undeploying application: "
					+ wrapper.getUndeployPath());
		}
	}

	/**
	 * Deploy the new admin-console application.
	 * 
	 * @param mgrInfo
	 *            the tomcat manager app
	 * @param proxyInfo
	 *            the proxy info (may be <code>null</code>)
	 * @param warStream
	 *            the WAR file
	 */
	private void deployAdminConsole(TomcatManagerInfo mgrInfo,
			ProxyInfo proxyInfo, InputStream warStream) {

		ManagerWrapper wrapper = new ManagerWrapper(mgrInfo, appInfo, proxyInfo);
		wrapper.deployAdminConsole(warStream);
		DesignerLog.logInfo("Deployed new version: "
				+ wrapper.getNewTagDeployPath());
	}

	/**
	 * Asynchronous execution of an {@link Action}
	 * 
	 * @param action
	 */
	protected static void showResults(final Action action) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				action.run();
			}
		});
	}

	/**
	 * Loads the manager info from the local directory.
	 * <p>
	 * It expects to find file {@link IFileNames#MANAGER_INFO} file in the
	 * working folder. The file contains 2 lines: 1st contains the server's base
	 * url, the 2nd the username and password in the form
	 * <code>username:password</code>
	 * </p>
	 * 
	 * @return {@link TomcatManagerInfo} the loaded info
	 * @throws Exception
	 *             on error
	 */
	private final TomcatManagerInfo getManagerInfo() throws Exception {

		File infoFile = new File(".", IFileNames.MANAGER_INFO);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(infoFile));
			String serverUrl = in.readLine();
			if (StringUtils.isEmpty(serverUrl)
					|| !serverUrl.contains("http://")) {
				throw new IllegalArgumentException(infoFile.getAbsolutePath()
						+ ": First line ('" + serverUrl + "') is not a URL");
			}
			String nameLine = in.readLine();
			if (StringUtils.isEmpty(nameLine) || !nameLine.contains(":")) {
				throw new IllegalArgumentException(infoFile.getAbsolutePath()
						+ ": Second line ('" + nameLine
						+ "') does not contain credentials");
			}
			String[] namePwd = nameLine.trim().split("\\:");
			return new TomcatManagerInfo(serverUrl.trim(), namePwd[0],
					namePwd[1]);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Returns proxy information (if any) for the given manager URL.
	 * 
	 * @param mgrInfo
	 *            the Tomcat manager info
	 * @return IProxyData the proxy information or <code>null</code>
	 */
	private final ProxyInfo getProxyInfo(TomcatManagerInfo mgrInfo) {
		ServiceTracker proxyTracker = new ServiceTracker(Platform.getBundle(
				"org.eclipse.core.net").getBundleContext(), IProxyService.class
				.getName(), null);
		proxyTracker.open();
		IProxyService proxyService = (IProxyService) proxyTracker.getService();
		IProxyData[] proxyDataArray = proxyService.getProxyDataForHost(mgrInfo
				.getServerUrl());
		ProxyInfo proxyInfo = null;
		if (proxyDataArray != null && proxyDataArray.length > 0) {
			proxyInfo = new ProxyInfo(proxyDataArray[0].getHost(),
					proxyDataArray[0].getPort());
		}
		return proxyInfo;
	}

	/**
	 * @return InputStream return the WAR stream from the classpath
	 * @throws MissingResourceException
	 *             if the WAR is not found in the classpath
	 */
	private final InputStream getWarStream() throws MissingResourceException {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(
				IFileNames.ADMIN_CONSOLE_WAR);
		if (in == null) {
			throw new MissingResourceException("WAR not found", this.getClass()
					.getName(), IFileNames.ADMIN_CONSOLE_WAR);
		}
		return in;
	}

	/**
	 * Delay the current thread.
	 * 
	 * @param millis
	 *            duration in millis
	 */
	private final void delay(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
}
