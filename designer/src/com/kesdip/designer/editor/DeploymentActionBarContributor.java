package com.kesdip.designer.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import com.kesdip.designer.action.MaximizeAction;

public class DeploymentActionBarContributor extends
		MultiPageEditorActionBarContributor {

	private List<String> globalActionKeys = new ArrayList<String>();
	private List<IAction> retargetActions = new ArrayList<IAction>();
	private ActionRegistry registry = new ActionRegistry();
	private boolean maxActionInitialized;

	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		declareGlobalActionKeys();
		this.maxActionInitialized = false;
	}

	/**
	 * Builds the actions.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		addRetargetAction(new DeleteRetargetAction());
		
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		if (!maxActionInitialized &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() != null) {
			addAction(new MaximizeAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()));
			maxActionInitialized = true;
		}
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		
		buildActions();

		// add a "View" menu after "Edit"
        MenuManager viewMenu = new MenuManager("View");
        viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
        viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
        
        menuManager.insertAfter("com.kesdip.designer.EditMenu", viewMenu);
	}

	/**
	 * Adds the retarded actions.
	 * 
	 * @param action
	 *            The action to add
	 */
	protected void addRetargetAction(RetargetAction action) {
		addAction(action);
		retargetActions.add(action);
		getPage().addPartListener(action);
		addGlobalActionKey(action.getId());
	}

	/**
	 * Adds global action key.
	 * 
	 * @param key
	 *            The key to add
	 */
	protected void addGlobalActionKey(String key) {
		globalActionKeys.add(key);
	}

	/**
	 * Adds to action registry an action.
	 * 
	 * @param action
	 *            The action to add
	 */
	protected void addAction(IAction action) {
		getActionRegistry().registerAction(action);
	}

	/**
	 * Gets the registry.
	 * 
	 * @return ActionRegistry The registry
	 */
	protected ActionRegistry getActionRegistry() {
		return registry;
	}

	/**
	 * Declares the global action keys.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.UNDO.getId());
		addGlobalActionKey(ActionFactory.REDO.getId());
		addGlobalActionKey(ActionFactory.CUT.getId());
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
		addGlobalActionKey(ActionFactory.DELETE.getId());
	}

	protected IAction getAction(String id) {
		return getActionRegistry().getAction(id);
	}

	@Override
	public void setActivePage(IEditorPart activeEditor) {
		if (activeEditor == null)
			return;
		
		ActionRegistry registry = (ActionRegistry) activeEditor
				.getAdapter(ActionRegistry.class);
		IActionBars bars = getActionBars();
		for (int i = 0; i < globalActionKeys.size(); i++) {
			String id = (String) globalActionKeys.get(i);
			bars.setGlobalActionHandler(id, registry.getAction(id));
		}
		getActionBars().updateActionBars();
	}

}
