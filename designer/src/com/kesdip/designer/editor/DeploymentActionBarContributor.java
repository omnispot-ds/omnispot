package com.kesdip.designer.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import com.kesdip.designer.action.MaximizeAction;

@SuppressWarnings("restriction")
public class DeploymentActionBarContributor extends
		MultiPageEditorActionBarContributor {

	/** The registry of the Contributor */
	private ActionRegistry registry = new ActionRegistry();
	/** The registry of the active page. Initialized when the page is switched or set */
	private ActionRegistry activePageRegistry;
	/** The registry of the multipage editor. Initialized when the editor is switched */
	private ActionRegistry rootEditorRegistry;
	/** Remember the active editor */
	protected IEditorPart rootEditor;

	private List<String> globalActionKeys = new ArrayList<String>();
	private List<IAction> retargetActions = new ArrayList<IAction>();
	private boolean maxActionInitialized;
	
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
	 * Indicates the existence of a global action identified by the specified key. This global
	 * action is defined outside the scope of this contributor, such as the Workbench's undo
	 * action, or an action provided by a workbench ActionSet. The list of global action keys
	 * is used whenever the active editor is changed ({@link #setActiveEditor(IEditorPart)}).
	 * Keys provided here will result in corresponding actions being obtained from the active
	 * editor's <code>ActionRegistry</code>, and those actions will be registered with the
	 * ActionBars for this contributor. The editor's action handler and the global action must
	 * have the same key.
	 * @param key the key identifying the global action
	 */
	public void addGlobalActionKey(String key) {
		globalActionKeys.add(key);
	}

	/**
	 * Adds the specified RetargetAction to this contributors <code>ActionRegistry</code>. The
	 * RetargetAction is also added as a <code>IPartListener</code> of the contributor's page.
	 * Also, the retarget action's ID is flagged as a global action key, by calling {@link
	 * #addGlobalActionKey(String)}.
	 * @param action the retarget action being added
	 */
	public void addRetargetAction(RetargetAction action) {
		addAction(action);
		retargetActions.add(action);
		getPage().addPartListener(action);
		addGlobalActionKey(action.getId());
	}

	/**
	 * Declares the global action keys.
	 * 
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		addGlobalActionKey(ActionFactory.CUT.getId());
		addGlobalActionKey(ActionFactory.COPY.getId());
		addGlobalActionKey(ActionFactory.PASTE.getId());
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
		
		addRetargetAction(new MatchWidthRetargetAction());
		addRetargetAction(new MatchHeightRetargetAction());
		
		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, 
				GEFMessages.ToggleSnapToGeometry_Label, IAction.AS_CHECK_BOX));

		addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, 
				GEFMessages.ToggleGrid_Label, IAction.AS_CHECK_BOX));
		
		if (!maxActionInitialized &&
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().getActiveEditor() != null) {
			addAction(new MaximizeAction(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().getActiveEditor()));
			maxActionInitialized = true;
		}
	}

	/**
	 * Disposes the contributor. Removes all {@link RetargetAction}s that were {@link
	 * org.eclipse.ui.IPartListener}s on the {@link org.eclipse.ui.IWorkbenchPage} and 
	 * disposes them. Also disposes the action registry.
	 * <P>
	 * Subclasses may extend this method to perform additional cleanup.
	 * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
	 */
	public void dispose() {
		for (int i = 0; i < retargetActions.size(); i++) {
			RetargetAction action = (RetargetAction) retargetActions.get(i);
			getPage().removePartListener(action);
			action.dispose();
		}
		registry.dispose();
		retargetActions = null;
		registry = null;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return ActionRegistry The registry
	 */
	protected ActionRegistry getActionRegistry() {
		return registry;
	}

	protected IAction getAction(String id) {
		return getActionRegistry().getAction(id);
	}
	
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		declareGlobalActionKeys();
		this.maxActionInitialized = false;
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		
		buildActions();

		// add a "View" menu after "Edit"
        MenuManager viewMenu = new MenuManager("View");
        viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
        viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
    	viewMenu.add(new Separator());
    	viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
    	viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
    	viewMenu.add(new Separator());
    	viewMenu.add(getAction(GEFActionConstants.MATCH_WIDTH));
    	viewMenu.add(getAction(GEFActionConstants.MATCH_HEIGHT));
        
    	menuManager.insertAfter("com.kesdip.designer.EditMenu", viewMenu);
	}

    /**
     * Sets the active page of the the multi-page editor to be the given editor.
     * Redirect actions to the given editor if actions are not already being sent to it.
     * <p>
     * This method is called whenever the page changes (from MultiPageEditorPart.pageChange(int)). 
     * Subclasses must implement this method to redirect actions to the given 
     * editor (if not already directed to it).
     * </p>
     *
     * @param activeEditor the new active editor, or <code>null</code> if there is no active page, or if the
     *   active page does not have a corresponding editor
     */
	public void setActivePage(IEditorPart editor) {
		activePageRegistry = (ActionRegistry)editor.getAdapter(ActionRegistry.class);
		// Connect the actions
		connectActions();
	}
    /* (non-JavaDoc)
     * Method declared on EditorActionBarContributor
     * Registers the contributor with the multi-page editor for future 
     * editor action redirection when the active page is changed, and sets
     * the active page.
     */
    public void setActiveEditor(IEditorPart editor) {
        rootEditor = editor;
        super.setActiveEditor(editor);
		// Switch the current page registry
        rootEditorRegistry = (ActionRegistry) editor.getAdapter(ActionRegistry.class);
		// Connect the actions
		connectActions();
    }

	/**
	 * Connect the actions registered in the globalActionKeys.
	 * Lookup actions implementation in the rootEditor registry and in the current page registry.
	 */
	protected void connectActions() {
		IActionBars bars = getActionBars();
		Iterator<String> iter = globalActionKeys.iterator();
		while (iter.hasNext()) {
			String id = iter.next();
			bars.setGlobalActionHandler(id, getEditorAction(id));
		}
		bars.updateActionBars();
	}

	/**
	 * Get the action from one of the registry
	 * @param key
	 */
  protected IAction getEditorAction(String key)
  {
	  IAction action = null;
	  
	  if (activePageRegistry!= null)
		  action = activePageRegistry.getAction(key);
	  if (action == null && rootEditorRegistry != null)
		  action = rootEditorRegistry.getAction(key);
	  
	  return action;
  }

}
