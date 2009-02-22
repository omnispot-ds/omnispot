package com.kesdip.designer.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.utils.FileUtils;
import com.kesdip.designer.view.DeploymentView;

public class OpenFileHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog dialog = new FileDialog(
				HandlerUtil.getActiveShell(event), SWT.OPEN | SWT.APPLICATION_MODAL);
		dialog.setFilterNames(new String[] { "Koutra Designer Files", "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
		String path = dialog.open();
		DesignerLog.logInfo("User entered path: " + path);
		File f = new File(path);
		try {
			IFile deploymentFile = FileUtils.getFile(f);
			
			Deployment input = loadInputFromFile(deploymentFile);
			
			DeploymentView dv = (DeploymentView) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().
				showView("com.kesdip.designer.DeploymentView");
			dv.setDeployment(input, path);
		} catch (Exception e) {
			DesignerLog.logError("Unable to open editor for: " + path, e);
		}
		return null;
	}

	private Deployment loadInputFromFile(IFile f) {
		Deployment retVal;
		try {
			retVal = new Deployment();
			retVal.deserialize(f.getContents());
		} catch (Exception e) { 
			DesignerLog.logError("Unable to load model.", e);
			retVal = new Deployment();
		}
		
		return retVal;
	}
}
