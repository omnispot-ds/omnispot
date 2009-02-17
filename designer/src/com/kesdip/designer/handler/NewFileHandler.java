package com.kesdip.designer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.view.DeploymentView;

public class NewFileHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Deployment input = new Deployment();
			
			DeploymentView dv = (DeploymentView) PlatformUI.getWorkbench().
				getActiveWorkbenchWindow().getActivePage().
				showView("com.kesdip.designer.DeploymentView");
			dv.setDeployment(input, null);
		} catch (Exception e) {
			DesignerLog.logError("Unable to create new deployment", e);
		}
		return null;
	}

}
