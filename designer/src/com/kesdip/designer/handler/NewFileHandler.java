package com.kesdip.designer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.utils.DesignerLog;

public class NewFileHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Deployment input = new Deployment();
			DeploymentEditorInput dei = new DeploymentEditorInput(input, null);
			
			
			IDE.openEditor(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getActivePage(), dei,
					"com.kesdip.designer.DeploymentEditor");
		} catch (Exception e) {
			DesignerLog.logError("Unable to create new deployment", e);
		}
		return null;
	}

}
