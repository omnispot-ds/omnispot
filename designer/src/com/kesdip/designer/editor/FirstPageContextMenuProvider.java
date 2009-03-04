package com.kesdip.designer.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.DeleteLayoutAction;
import com.kesdip.designer.action.MoveDownAction;
import com.kesdip.designer.action.MoveUpAction;

public class FirstPageContextMenuProvider extends ContextMenuProvider {
	private ActionRegistry actionRegistry;

	public FirstPageContextMenuProvider(EditPartViewer viewer,
			ActionRegistry actionRegistry) {
		super(viewer);
		this.actionRegistry = actionRegistry;
	}
	
	@Override
	public void buildContextMenu(IMenuManager menu) {
		// Add standard action groups to the menu
		GEFActionConstants.addStandardActionGroups(menu);
		
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO,
				actionRegistry.getAction(ActionFactory.UNDO.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO,
				actionRegistry.getAction(ActionFactory.REDO.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY,
				actionRegistry.getAction(ActionFactory.CUT.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY,
				actionRegistry.getAction(ActionFactory.COPY.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_COPY,
				actionRegistry.getAction(ActionFactory.PASTE.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
				actionRegistry.getAction(ActionFactory.DELETE.getId()));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
				actionRegistry.getAction(CreateLayoutAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
				actionRegistry.getAction(DeleteLayoutAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
				actionRegistry.getAction(MoveUpAction.ID));
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
				actionRegistry.getAction(MoveDownAction.ID));
	}

}
