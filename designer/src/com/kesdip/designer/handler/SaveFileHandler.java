package com.kesdip.designer.handler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.view.DeploymentView;

public class SaveFileHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			DeploymentView dv = (DeploymentView) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().
				showView("com.kesdip.designer.DeploymentView");
			Deployment deployment = dv.getDeployment();
			
			String path = dv.getDeploymentPath();
			DesignerLog.logInfo("File being edited path: " + path);

			OutputStream os = new BufferedOutputStream(new FileOutputStream(path));
			deployment.serialize(os);
			os.close();
		} catch (Exception e) {
			DesignerLog.logError("Unable to save file", e);
		}
		return null;
	}

}
