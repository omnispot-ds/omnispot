package com.kesdip.designer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class DeleteLayoutAction extends Action {
	
	private Deployment deployment;
	private IStructuredSelection selection;

	public DeleteLayoutAction(Deployment deployment, IStructuredSelection selection) {
		super("Delete Layout");
		this.deployment = deployment;
		this.selection = selection;
	}

	@Override
	public void run() {
		for (Object sel : selection.toList()) {
			if (!(sel instanceof Layout))
				continue;
			Layout l = (Layout) sel;
			deployment.removeLayout(l);
		}
	}
}
