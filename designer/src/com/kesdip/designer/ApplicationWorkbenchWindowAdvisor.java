package com.kesdip.designer;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.kesdip.designer.update.AdminConsoleUpdateCompletionAction;
import com.kesdip.designer.update.AdminConsoleUpdateJob;
import com.kesdip.designer.update.VersionUpdateChecker;
import com.kesdip.designer.utils.DesignerLog;

/**
 * Decorates the Workbench window and checks if the Designer plugin has been
 * updated before the last restart.
 * 
 * @author pftakas
 * @author gerogias
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowProgressIndicator(false);
	}

	/**
	 * Hide the "Run" menu and trigger the version check.
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowCreate()
	 */
	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		// hide the "Run" menu item contributed by org.eclipse.ui.externaltools
		getWindowConfigurer().getWindow().getActivePage().hideActionSet(
				"org.eclipse.ui.externaltools.ExternalToolsSet");
		checkVersion();
	}

	/**
	 * Checks the current version of the plugin with the last known version. If
	 * it has been updated, it schedules a job to remotely update the
	 * AdminConsole with the latest WAR.
	 * 
	 * @see http://blog.eitchnet.ch/?p=46
	 * @see http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DialogExamples.htm
	 */
	private final void checkVersion() {
		// check if the plugin has been updated
		String version = (String) Activator.getDefault().getBundle()
				.getHeaders().get("Bundle-Version");
		VersionUpdateChecker updateChecker = new VersionUpdateChecker(
				Activator.PLUGIN_ID, version);
		// plugin was updated, schedule an AdminConsole update
		if (updateChecker.isVersionUpdated()) {
			ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(
					getWindowConfigurer().getWindow().getShell());
			AdminConsoleUpdateCompletionAction completionAction = new AdminConsoleUpdateCompletionAction(
					getWindowConfigurer().getWindow().getShell());
			AdminConsoleUpdateJob job = new AdminConsoleUpdateJob(
					completionAction, updateChecker.getVersion(), updateChecker
							.getPreviousVersion());
			try {
				monitorDialog.run(true, true, job);
			} catch (Exception e) {
				DesignerLog.logError("Error updating software", e);
			}
		}
	}
}
