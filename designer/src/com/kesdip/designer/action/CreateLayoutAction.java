package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import com.kesdip.designer.command.LayoutCreationCommand;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class CreateLayoutAction extends SelectionAction {
	public static final String ID = "com.kesdip.designer.action.CreateLayoutAction";
	
	public CreateLayoutAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@Override
	public void run() {
		execute(createLayoutCreationCommand(getSelectedObjects()));
	}
	
	@Override
	protected boolean calculateEnabled() {
		Command cmd = createLayoutCreationCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	@SuppressWarnings("unchecked")
	private Command createLayoutCreationCommand(List objects) {
		if (objects == null || objects.size() != 1)
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;
		if (!(((EditPart) objects.get(0)).getModel() instanceof Deployment))
			return null;
		Deployment d = (Deployment) ((EditPart) objects.get(0)).getModel();
		return new LayoutCreationCommand(new Layout(), d);
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setId(ID);
		setText("Create Layout");
		setEnabled(false);
	}

}
