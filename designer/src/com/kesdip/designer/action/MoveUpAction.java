package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;

public class MoveUpAction extends SelectionAction {
	public static final String ID = "com.kesdip.designer.action.MoveUp";

	/**
	 * Constructs a <code>MPEditorMoveUpAction</code> using the specified part.
	 * @param part The part for this action
	 */
	public MoveUpAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	/**
	 * Returns <code>true</code> if the selected objects can
	 * be moved up.  Returns <code>false</code> if there are
	 * no objects selected or the selected objects are not
	 * {@link EditPart}s.
	 * @return <code>true</code> if the command should be enabled
	 */
	protected boolean calculateEnabled() {
		Command cmd = createMoveUpCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	/**
	 * Create a command to move the selected objects up.
	 * @param objects The objects to be moved up.
	 * @return The command to move the selected objects up.
	 */
	@SuppressWarnings("unchecked")
	public Command createMoveUpCommand(List objects) {
		if (objects.isEmpty() || objects.size() != 1)
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;
		EditPart editPart = (EditPart) objects.get(0);

		GroupRequest moveUpReq =
			new GroupRequest(DesignerComponentEditPolicy.REQ_MOVE_UP);
		moveUpReq.setEditParts(objects);

		return editPart.getCommand(moveUpReq);
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setText("Move Up");
		setToolTipText("Move element up in the child sequence of the parent");
		setId(ID);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UP));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_UP_DISABLED));
		setEnabled(false);
	}

	/**
	 * Performs the move up action on the selected objects.
	 */
	public void run() {
		execute(createMoveUpCommand(getSelectedObjects()));
	}

}
