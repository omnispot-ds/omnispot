package com.kesdip.designer.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;

public class OpenFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog dialog = new FileDialog(
				HandlerUtil.getActiveShell(event), SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterNames(new String[] { "Omni-Spot Designer Files", "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
		String path = dialog.open();
		DesignerLog.logInfo("User entered path: " + path);
		if (path == null)
			return null;
		
		File f = new File(path);
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(f));
			Deployment input = new Deployment();
			try {
				input.deserialize(is);
			} finally {
				is.close();
			}
			DeploymentEditorInput dei = new DeploymentEditorInput(input, path);
			
			IDE.openEditor(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage(), dei,
					"com.kesdip.designer.DeploymentEditor");
		} catch (Exception e) {
			DesignerLog.logError("Unable to open editor for: " + path, e);
			MessageDialog.openError(HandlerUtil.getActiveShell(event),
					"Designer file format error", "Unable to load file: " + path +
					". This is probably not an Omni-Spot Designer file. Please " +
					"check the error log for more details.");
		}
		return null;
	}

}
