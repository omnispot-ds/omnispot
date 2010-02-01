/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 06 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.update;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

/**
 * A runnable implementation to launch the update UI.
 * 
 * @author gerogias
 */
public class UpdateJobRunner implements Runnable {

	/**
	 * The parent window.
	 */
	private IWorkbenchWindow window = null;
	
	/**
	 * Default constructor.
	 * @param window the parent window
	 */
	public UpdateJobRunner(IWorkbenchWindow window) {
		this.window = window;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		UpdateJob job = new UpdateJob("Search for update", false, false);//$NON-NLS-1$ 
		job.addJobChangeListener(new SoftwareUpdateJobListener());
		UpdateManagerUI.openInstaller(window.getShell(), job);
		PlatformUI.getWorkbench().getProgressService().showInDialog(
				window.getShell(), job);
	}
}
