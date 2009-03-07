package com.kesdip.designer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.kesdip.designer.utils.DesignerLog;
import com.kesdip.designer.utils.FileUtils;
import com.kesdip.designer.wizards.DesignerNewWizard;

public class NewFileHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			DesignerNewWizard wizard = new DesignerNewWizard();
			wizard.init(PlatformUI.getWorkbench(),
					new StructuredSelection(FileUtils.getTempProject()));
			WizardDialog dialog = new WizardDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					wizard);
			dialog.open();
		} catch (Exception e) {
			DesignerLog.logError("Unable to create new deployment", e);
		}
		return null;
	}

}
