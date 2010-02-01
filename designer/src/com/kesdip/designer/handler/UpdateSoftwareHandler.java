/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 06 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.update.UpdateJobRunner;

/**
 * Launches the software update wizard.
 * 
 * @author gerogias
 */
public class UpdateSoftwareHandler extends AbstractHandler {

	/**
	 * Launches a blocking installer UI.
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent executionEvent)
			throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindow(executionEvent);
		// launch the installer UI (spawns a new thread)
		UpdateJobRunner runner = new UpdateJobRunner(window);
		BusyIndicator.showWhile(window.getShell().getDisplay(), runner);
		return null;
	}

	/**
	 * Always enabled.
	 * 
	 * @return always <code>true</code>
	 * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
