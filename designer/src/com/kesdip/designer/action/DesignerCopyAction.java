package com.kesdip.designer.action;

import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import com.kesdip.designer.parts.OutlineRootPart;
import com.kesdip.designer.parts.PageOneRootPart;

@SuppressWarnings("restriction")
public class DesignerCopyAction extends SelectionAction {
	
	public DesignerCopyAction(IEditorPart editor) {
		this((IWorkbenchPart)editor);
	}
	
	public DesignerCopyAction(IEditorPart editor, String label) {
		this((IWorkbenchPart)editor);
		setText(label);
	}
	
	public DesignerCopyAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}

	@Override
	public void run() {
		Clipboard.getDefault().setContents(getSelectedObjects());
	}

	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects() == null || getSelectedObjects().size() == 0)
			return false;
		
		for (Object o : getSelectedObjects()) {
			if (o instanceof PageOneRootPart || o instanceof OutlineRootPart)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setId(ActionFactory.COPY.getId());
		setText(GEFMessages.CopyAction_Label);
		setToolTipText(GEFMessages.CopyAction_Tooltip);
		setActionDefinitionId("org.eclipse.ui.edit.copy");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_COPY_DISABLED));
		setEnabled(false);
	}
}
