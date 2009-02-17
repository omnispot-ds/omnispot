package com.kesdip.designer.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public class DesignerEditorContentMenuProvider extends ContextMenuProvider {

	public DesignerEditorContentMenuProvider(EditPartViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);
		
		// Add actions to the menu
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, // target group id
				ActionFactory.UNDO.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				ActionFactory.REDO.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				ActionFactory.DELETE.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow()));
	}

}
