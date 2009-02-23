package com.kesdip.designer.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

import com.kesdip.designer.action.DesignerCopyAction;
import com.kesdip.designer.action.DesignerCutAction;
import com.kesdip.designer.action.DesignerPasteAction;

public class DesignerEditorContentMenuProvider extends ContextMenuProvider {

	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
		
	public DesignerEditorContentMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		if (registry == null) {
			throw new IllegalArgumentException();
		}
		actionRegistry = registry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);
		
		// Add actions to the menu
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, // target group id
				getAction(ActionFactory.UNDO.getId())); // action to add
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(DesignerCutAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(DesignerCopyAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(DesignerPasteAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
