package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.WorkbenchMessages;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;

@SuppressWarnings("restriction")
public class DesignerCutAction extends SelectionAction {
	
	public DesignerCutAction(IEditorPart editor) {
		this((IWorkbenchPart)editor);
	}
	
	public DesignerCutAction(IEditorPart editor, String label) {
		this((IWorkbenchPart)editor);
		setText(label);
	}
	
	public DesignerCutAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@Override
	public void run() {
		Clipboard.getDefault().setContents(getSelectedObjects());
		execute(createRemoveSelectionCommand(getSelectedObjects()));
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createRemoveSelectionCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	@SuppressWarnings("unchecked")
	private Command createRemoveSelectionCommand(List objects) {
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;

		GroupRequest pasteReq =
			new GroupRequest(DesignerComponentEditPolicy.REQ_CUT);
		pasteReq.setEditParts(objects);

		CompoundCommand compoundCmd = new CompoundCommand("cut components");
		for (int i = 0; i < objects.size(); i++) {
			EditPart object = (EditPart) objects.get(i);
			Command cmd = object.getCommand(pasteReq);
			if (cmd != null) compoundCmd.add(cmd);
		}

		if (compoundCmd.size() != 0)
			return (Command) compoundCmd.getChildren()[0];
		return null;
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setId(ActionFactory.CUT.getId());
		setText(WorkbenchMessages.Workbench_cut);
		setToolTipText(WorkbenchMessages.Workbench_cutToolTip);
		setActionDefinitionId("org.eclipse.ui.edit.cut");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_CUT_DISABLED));
		setEnabled(false);
	}
}
