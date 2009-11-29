package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.kesdip.designer.command.LayoutDeletionCommand;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;

public class DeleteLayoutAction extends SelectionAction {
	public static final String ID = "com.kesdip.designer.action.DeleteLayoutAction";
	
	public DeleteLayoutAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	public void run() {
		execute(createLayoutDeletionCommand(getSelectedObjects()));
	}
	
	@Override
	protected boolean calculateEnabled() {
		Command cmd = createLayoutDeletionCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	@SuppressWarnings("unchecked")
	private Command createLayoutDeletionCommand(List objects) {
		Deployment deployment = null;
		for (Object o : objects) {
			if (!(o instanceof EditPart))
				continue;
			if (!(((EditPart) o).getModel() instanceof ModelElement))
				continue;
			
			ModelElement e = (ModelElement) ((EditPart) o).getModel();
			if (!(e instanceof Layout)) {
				return null;
			}
			Layout l = (Layout) e;
			Deployment d = (Deployment) l.getParent();
			if (d == null) {
				return null;
			} else {
				deployment = d;
			}
		}
		if (deployment == null)
			return null;
		return new LayoutDeletionCommand(objects, deployment);
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setId(ID);
		setText("Delete Layout");
		setEnabled(false);
	}
}
