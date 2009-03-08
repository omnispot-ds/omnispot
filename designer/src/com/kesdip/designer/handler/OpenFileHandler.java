package com.kesdip.designer.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.utils.FileUtils;

public class OpenFileHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog dialog = new FileDialog(
				HandlerUtil.getActiveShell(event), SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterNames(new String[] { "Ke.S.Di.P. E.P.E. Designer Files", "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
		String path = dialog.open();
		DesignerLog.logInfo("User entered path: " + path);
		File f = new File(path);
		try {
			IFile deploymentFile = FileUtils.getFile(f);

			Deployment input = new Deployment();
			input.deserialize(deploymentFile.getContents());
			DeploymentEditorInput dei = new DeploymentEditorInput(input, path);
			
			IDE.openEditor(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage(), dei,
					"com.kesdip.designer.DeploymentEditor");
		} catch (Exception e) {
			DesignerLog.logError("Unable to open editor for: " + path, e);
			MessageDialog.openError(HandlerUtil.getActiveShell(event),
					"Designer file format error", "Unable to load file: " + path +
					". This is probably not a Ke.S.Di.P. E.P.E. Designer file. Please " +
					"check the error log for more details.");
		}
		return null;
	}

}
