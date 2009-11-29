package com.kesdip.designer.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.MoveDownAction;
import com.kesdip.designer.action.MoveUpAction;
import com.kesdip.designer.action.MaximizeAction;

public class DesignerEditorContentMenuProvider extends ContextMenuProvider {

	/** The editor's action registry. */
	private ActionRegistry actionRegistry;
		
	public DesignerEditorContentMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		actionRegistry = registry;
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);
		
		if (actionRegistry == null)
			return;
		
		// Add actions to the menu
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, // target group id
				getAction(ActionFactory.UNDO.getId())); // action to add
		menu.appendToGroup(
				GEFActionConstants.GROUP_UNDO, 
				getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(ActionFactory.CUT.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_COPY, 
				getAction(ActionFactory.PASTE.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(ActionFactory.DELETE.getId()));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(CreateLayoutAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(MoveUpAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(MoveDownAction.ID));
		menu.appendToGroup(
				GEFActionConstants.GROUP_EDIT,
				getAction(MaximizeAction.ID));
		
		// Alignment Actions
		MenuManager submenu = new MenuManager("Alignment");

		IAction action = getAction(GEFActionConstants.ALIGN_LEFT);
		if (action.isEnabled())
			submenu.add(action);

		action = getAction(GEFActionConstants.ALIGN_CENTER);
		if (action.isEnabled())
			submenu.add(action);

		action = getAction(GEFActionConstants.ALIGN_RIGHT);
		if (action.isEnabled())
			submenu.add(action);
			
		submenu.add(new Separator());
		
		action = getAction(GEFActionConstants.ALIGN_TOP);
		if (action.isEnabled())
			submenu.add(action);

		action = getAction(GEFActionConstants.ALIGN_MIDDLE);
		if (action.isEnabled())
			submenu.add(action);

		action = getAction(GEFActionConstants.ALIGN_BOTTOM);
		if (action.isEnabled())
			submenu.add(action);

		menu.appendToGroup(GEFActionConstants.GROUP_REST, submenu);
	}

	private IAction getAction(String actionId) {
		return actionRegistry.getAction(actionId);
	}

}
