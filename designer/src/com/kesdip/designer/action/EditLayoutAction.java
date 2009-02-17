package com.kesdip.designer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.utils.DesignerLog;

public class EditLayoutAction extends Action {

	private IStructuredSelection selection;

	public EditLayoutAction(IStructuredSelection selection) {
		super("Edit Layout");
		this.selection = selection;
	}

	@Override
	public void run() {
		for (Object sel : selection.toList()) {
			if (!(sel instanceof Layout))
				continue;
 			Layout l = (Layout) sel;
 			LayoutEditorInput input = new LayoutEditorInput(l);
 			try {
				IDE.openEditor(PlatformUI.getWorkbench().
						getActiveWorkbenchWindow().getActivePage(), input,
						"com.kesdip.designer.DesignerEditor");
 			} catch (Exception e) {
 				DesignerLog.logError("Unable to open editor.", e);
 			}
		}
	}

}
