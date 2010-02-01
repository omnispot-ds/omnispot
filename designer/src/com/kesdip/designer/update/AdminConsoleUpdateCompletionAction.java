/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 08 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.update;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kesdip.designer.Activator;

/**
 * An action to be triggered by the completion of {@link AdminConsoleUpdateJob}.
 * 
 * @author gerogias
 * @see http://blog.eitchnet.ch/?p=46
 */
public class AdminConsoleUpdateCompletionAction extends Action {

	private boolean ok;
	private String okTitle;
	private String okMsg;
	private String failMsg;
	private String failTitle;
	private Throwable throwable;

	private Shell shell;
	private String pluginId;

	/**
	 * Default constructor.
	 * 
	 * @param shell
	 */
	public AdminConsoleUpdateCompletionAction(Shell shell) {
		this.pluginId = Activator.PLUGIN_ID;
		this.shell = shell;
		okTitle = "Job successful";
		okMsg = "Sucessfully updated AdminConsole";
		failTitle = "Job failed";
		failMsg = "Failed to update AdminConsole";

	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public void run() {
		// show the dialog
		if (ok) {
			MessageDialog.openInformation(shell, okTitle, okMsg);
		} else {
			Status status = new Status(IStatus.ERROR, pluginId, throwable
					.getLocalizedMessage(), throwable);
			ErrorDialog.openError(shell, failTitle, failMsg, status);
		}
	}

	/**
	 * @param ok
	 *            the ok to set
	 */
	public void setOk(boolean ok) {
		this.ok = ok;
	}

}
