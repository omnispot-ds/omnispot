package com.kesdip.designer.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.gef.ui.actions.MatchWidthAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.DeleteLayoutAction;
import com.kesdip.designer.action.DesignerCopyAction;
import com.kesdip.designer.action.DesignerCutAction;
import com.kesdip.designer.action.DesignerPasteAction;
import com.kesdip.designer.action.MoveDownAction;
import com.kesdip.designer.action.MoveUpAction;
import com.kesdip.designer.action.MaximizeAction;
import com.kesdip.designer.action.ToggleGridAction;
import com.kesdip.designer.action.ToggleSnapToGeometryAction;
import com.kesdip.designer.handler.DeploymentEditorInput;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.parts.OutlineLayoutPart;
import com.kesdip.designer.parts.OutlinePartFactory;
import com.kesdip.designer.utils.DesignerLog;

@SuppressWarnings("restriction")
public class DeploymentEditor extends MultiPageEditorPart implements
		PropertyChangeListener, ISelectionChangedListener, CommandStackListener,
		IPageChangeProvider {
	
	private Deployment model;
	private TreeViewer outlineViewer;
	private SelectionSynchronizer synchronizer;
	private ActionRegistry actionRegistry;
	private MultiPageCommandStackListener multiPageCommandStackListener;
	private DelegatingCommandStack delegatingCommandStack;
	private DelegatingZoomManager delegatingZoomManager;
    private CommandStackListener delegatingCommandStackListener;
    private ISelectionListener selectionListener;
	private boolean isDirty;
	private Map<Layout, Integer> pagesMap;
	private Map<Layout, LayoutEditor> pageEditorsMap;
	private IContentOutlinePage outlinePage;

	public DeploymentEditor() {
		actionRegistry = new ActionRegistry();
		delegatingCommandStackListener = new CommandStackListener() {
	        public void commandStackChanged(EventObject event)
	        {
	            updateActions();
	        }
	    };
	    selectionListener = new ISelectionListener()
	    {
	        public void selectionChanged(IWorkbenchPart part, ISelection selection)
	        {
	            updateActions();
	        }
	    };
	    multiPageCommandStackListener = new MultiPageCommandStackListener();
	    pagesMap = new HashMap<Layout, Integer>();
	    pageEditorsMap = new HashMap<Layout, LayoutEditor>();
	}
	
	public Deployment getModel() {
		return model;
	}
	
	/**
	 * Returns the selection synchronizer object. The synchronizer can be used to sync the
	 * selection of 2 or more EditPartViewers.
	 * @return the synchronizer
	 */
	public SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new SelectionSynchronizer();
		return synchronizer;
	}
	
	@SuppressWarnings("unchecked")
	protected void updateActions() {
		Iterator iter = actionRegistry.getActions();
		while (iter.hasNext()) {
			IAction action = (IAction) iter.next();
			if (action instanceof UpdateAction)
				((UpdateAction)action).update();
		}
	}

	
	public void commandStackChanged(EventObject event) {
		updateActions();
		updateMenus();
	}
	
    private EditorPart getCurrentPage()
    {
        if (getActivePage() == -1)
            return null;

        return (EditorPart) getEditor(getActivePage());
    }
    
    protected DelegatingZoomManager getDelegatingZoomManager()
    {
        if (null == delegatingZoomManager)
        {
            delegatingZoomManager = new DelegatingZoomManager();
            if (null != getCurrentPage()) {
            	if (getCurrentPage() instanceof LayoutEditor) {
            		LayoutEditor le = (LayoutEditor) getCurrentPage();
	                delegatingZoomManager.setCurrentZoomManager(
	                    getZoomManager(le.getViewer()));
            	}
            }
        }

        return delegatingZoomManager;
    }

    private ZoomManager getZoomManager(GraphicalViewer viewer)
    {
        // get zoom manager from root edit part
        RootEditPart rootEditPart = viewer.getRootEditPart();
        ZoomManager zoomManager = null;
        if (rootEditPart instanceof ScalableFreeformRootEditPart)
        {
            zoomManager =
                ((ScalableFreeformRootEditPart) rootEditPart).getZoomManager();
        }
        else if (rootEditPart instanceof ScalableRootEditPart)
        {
            zoomManager =
                ((ScalableRootEditPart) rootEditPart).getZoomManager();
        }
        return zoomManager;
    }
    
    protected DelegatingCommandStack getDelegatingCommandStack()
    {
        if (null == delegatingCommandStack)
        {
            delegatingCommandStack = new DelegatingCommandStack();
            if (null != getCurrentPage())
            	if (getCurrentPage() instanceof DesignerEditorFirstPage) {
            		DesignerEditorFirstPage page =
            			(DesignerEditorFirstPage) getCurrentPage();
            		delegatingCommandStack.setCurrentCommandStack(page.getCommandStack());
            	} else if (getCurrentPage() instanceof GraphicalEditor) {
            		delegatingCommandStack.setCurrentCommandStack(
            				(CommandStack) getCurrentPage().getAdapter(CommandStack.class));
            	} else {
            		throw new RuntimeException("Unexpected current page class: " +
            				getCurrentPage().getClass().getName());
            	}
        }

        return delegatingCommandStack;
    }
    
    private void updateMenus() {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		WorkbenchPage ip = (WorkbenchPage) page;
        IActionBars actionBars = ip.getActionBars();
        MenuManager menuManager = (MenuManager) actionBars.getMenuManager();
        menuManager.update(IAction.TEXT);
    }
    
	public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty)
        {
            this.isDirty = isDirty;
            firePropertyChange(IEditorPart.PROP_DIRTY);
            
            updateMenus();
        }
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	public void markSaveLocation() {
		multiPageCommandStackListener.markSaveLocations();
	}
	
	@Override
	protected void createPages() {
		try {
			DesignerEditorFirstPage firstPage =
				new DesignerEditorFirstPage(this, actionRegistry);
			addPage(firstPage, new NullEditorInput());
            multiPageCommandStackListener.addCommandStack(firstPage.getCommandStack());
            getDelegatingCommandStack().setCurrentCommandStack(
            		firstPage.getCommandStack());
			setPageText(0, "Deployment");
			
			int count = 1;
			for (ModelElement elem : model.getChildren()) {
				Layout l = (Layout) elem;
				addPageForLayout(count++, l);
			}
			setActivePage(0);
   		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof DeploymentEditorInput) {
			model = ((DeploymentEditorInput) input).getDeployment();
			model.addPropertyChangeListener(this);
			for (ModelElement elem : model.getChildren()) {
				Layout l = (Layout) elem;
				l.addPropertyChangeListener(this);
			}
			String path = ((DeploymentEditorInput) input).getPath();
			if (path != null) {
				File f = new File(path);
				setPartName(f.getName());
			} else {
				setPartName("New Deployment");
			}
		}
		
		actionRegistry.registerAction(new DeleteAction((IWorkbenchPart) this));
		actionRegistry.registerAction(new UndoAction(this));
		actionRegistry.registerAction(new RedoAction(this));
		actionRegistry.registerAction(new DesignerCutAction(this));
		actionRegistry.registerAction(new DesignerCopyAction(this));
		actionRegistry.registerAction(new DesignerPasteAction(this));
		actionRegistry.registerAction(new CreateLayoutAction(this));
		actionRegistry.registerAction(new DeleteLayoutAction(this));
		actionRegistry.registerAction(new MoveUpAction(this));
		actionRegistry.registerAction(new MoveDownAction(this));
		actionRegistry.registerAction(new MaximizeAction(this));
		actionRegistry.registerAction(new ZoomInAction(getDelegatingZoomManager()));
		actionRegistry.registerAction(new ZoomOutAction(getDelegatingZoomManager()));
		actionRegistry.registerAction(new MatchHeightAction(this));
		actionRegistry.registerAction(new MatchWidthAction(this));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE));
		actionRegistry.registerAction(
				new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM));
		actionRegistry.registerAction(new ToggleSnapToGeometryAction());
		actionRegistry.registerAction(new ToggleGridAction());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		
        getDelegatingCommandStack().addCommandStackListener(
                delegatingCommandStackListener);
        
        getSite()
	        .getWorkbenchWindow()
	        .getSelectionService()
	        .addSelectionListener(
	        selectionListener);
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
     */
    protected void pageChange(int newPageIndex)
    {
        super.pageChange(newPageIndex);

        // refresh content depending on current page
        currentPageChanged();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.MultiPageEditorPart#setActivePage(int)
     */
    protected void setActivePage(int pageIndex)
    {
        super.setActivePage(pageIndex);

        // refresh content depending on current page
        currentPageChanged();
    }
    
    public Layout getCurrentLayout() {
    	if (getCurrentPage() instanceof GraphicalEditor) {
    		LayoutEditor le = (LayoutEditor) getCurrentPage();
    		return le.getModel();
    	}
    	
    	return null;
    }
    
    /**
     * Indicates that the current page has changed.
     * <p>
     * We update the DelegatingCommandStack, OutlineViewer
     * and other things here.
     */
    protected void currentPageChanged()
    {
    	updateMenus();
    	
        // update delegating command stack
    	if (getCurrentPage() instanceof DesignerEditorFirstPage) {
    		DesignerEditorFirstPage page =
    			(DesignerEditorFirstPage) getCurrentPage();
    		delegatingCommandStack.setCurrentCommandStack(page.getCommandStack());
    		delegatingZoomManager.setCurrentZoomManager(null);
    	} else if (getCurrentPage() instanceof GraphicalEditor) {
    		LayoutEditor le = (LayoutEditor) getCurrentPage();
    		delegatingCommandStack.setCurrentCommandStack(
    				(CommandStack) getCurrentPage().getAdapter(CommandStack.class));
    		delegatingZoomManager.setCurrentZoomManager(
    				getZoomManager(le.getViewer()));
    		getSite().getSelectionProvider().setSelection(
    				new StructuredSelection(new OutlineLayoutPart(le.getModel())));
    		if (outlinePage instanceof DeploymentOutlinePage) {
    			((DeploymentOutlinePage) outlinePage).initialize(getCurrentPage());
    		}
    	} else {
    		throw new RuntimeException("Unexpected current page class: " +
    				getCurrentPage().getClass().getName());
    	}
    	
    	PageChangedEvent event = new PageChangedEvent(this, getCurrentPage());
    	for (IPageChangedListener l : listeners) {
    		l.pageChanged(event);
    	}
    }

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == ActionRegistry.class) {
			return actionRegistry;
		} else if (adapter == CommandStack.class) {
			return getDelegatingCommandStack();
		} else if (adapter == ZoomManager.class) {
			return getDelegatingZoomManager();
		} else if (adapter == IPropertySheetPage.class) {
			PropertySheetPage page = new PropertySheetPage();
			page.setRootEntry(new UndoablePropertySheetEntry(getDelegatingCommandStack()));
			return page;
		} else if (adapter == IContentOutlinePage.class) {
			outlineViewer = new TreeViewer();
			outlineViewer.setEditDomain(new DefaultEditDomain(this));
			outlineViewer.setEditPartFactory(new OutlinePartFactory());
			getSite().setSelectionProvider(outlineViewer);
			outlineViewer.addSelectionChangedListener(this);
			
			ContextMenuProvider menuManager =
				new DesignerEditorContentMenuProvider(outlineViewer, actionRegistry);
			menuManager.setRemoveAllWhenShown(true);
			outlineViewer.setContextMenu(menuManager);
			getSite().registerContextMenu(menuManager, outlineViewer);
			outlinePage = new DeploymentOutlinePage(
					outlineViewer, getSelectionSynchronizer(), model, actionRegistry);
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void dispose() {
        // dispose multi page command stack listener
        multiPageCommandStackListener.dispose();

        // remove delegating CommandStackListener
        getDelegatingCommandStack().removeCommandStackListener(
            delegatingCommandStackListener);

        // remove selection listener
        getSite()
            .getWorkbenchWindow()
            .getSelectionService()
            .removeSelectionListener(
            selectionListener);

        // disposy the ActionRegistry (will dispose all actions)
        actionRegistry.dispose();
        
		super.dispose();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IEditorPart activeEditor = getSite().getPage().getActiveEditor();
		if (this.equals(activeEditor)) {
			updateActions();
			updateMenus();
		}
	}
	
	private void saveLayoutProperties() {
		for (int i = 0; i < getPageCount(); i++) {
			IEditorPart editor = getEditor(i);
			if (editor instanceof LayoutEditor)
				((LayoutEditor) editor).saveProperties();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		final Deployment deployment = getModel();
		String path = ((DeploymentEditorInput) getEditorInput()).getPath();
		
		if (path == null) {
			Shell shell = getSite().getWorkbenchWindow().getShell();
			FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
			dialog.setFilterNames(new String[] { "Omni-Spot Designer Files", "All files (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
			path = dialog.open();
			
			if (path == null)
				return;
		}
		
		if (null == monitor)
			monitor = new NullProgressMonitor();

		monitor.beginTask("Saving " + path, 1);
        try {
			monitor.worked(1);
			saveLayoutProperties();
			OutputStream os = new BufferedOutputStream(
					new FileOutputStream(path));
			deployment.serialize(os, false);
			os.close();
			getDelegatingCommandStack().markSaveLocation();
			monitor.done();
		} catch (Exception e) {
			DesignerLog.logError("Unable to save file", e);
		}
	}

	@Override
	public void doSaveAs() {
		// Show a SaveAs dialog
		Shell shell = getSite().getWorkbenchWindow().getShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
		dialog.setFilterNames(new String[] { "Omni-Spot Designer Files", "All files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.des.xml", "*.*" });
		String path = dialog.open();
		
		if (path != null) {
			// try to save the editor's contents under a different file name
			final File newFile = new File(path);
			try {
				new ProgressMonitorDialog(shell).run(
						false, // don't fork
						false, // not cancelable
						new WorkspaceModifyOperation() { // run this operation
							public void execute(final IProgressMonitor monitor) {
								try {
									saveLayoutProperties();
									OutputStream os = new BufferedOutputStream(
											new FileOutputStream(newFile));
									getModel().serialize(os, false);
									os.close();
								} catch (Exception e) {
									DesignerLog.logError("Unable to save file", e);
								} 
							}
						});
				// set input to the new file
				setInput(new DeploymentEditorInput(getModel(), path));
				getDelegatingCommandStack().markSaveLocation();
			} catch (Exception e) {
				DesignerLog.logError("Unable to save file", e);
			}
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	private void addPageForLayout(int index, Layout l) {
		try {
			LayoutEditor editor = new LayoutEditor(this, getSelectionSynchronizer(), outlineViewer);
			addPage(index, editor, new LayoutEditorInput(l));
			pagesMap.put(l, index);
			pageEditorsMap.put(l, editor);
			multiPageCommandStackListener.addCommandStack(editor.getEditorCommandStack());
			l.addPropertyChangeListener(this);
			setPageText(index, l.getName());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	private LayoutEditor getEditorForLayout(Layout l) {
		return pageEditorsMap.get(l);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Deployment.LAYOUT_ADDED_PROP)) {
			Layout l = (Layout) evt.getNewValue();
			addPageForLayout(pagesMap.size() + 1, l);
		}
		if (evt.getPropertyName().equals(Deployment.LAYOUT_REMOVED_PROP)) {
			Layout l = (Layout) evt.getNewValue();
			int pageIndex = pagesMap.get(l);
			removePage(pageIndex);
			pagesMap.remove(l);
			l.removePropertyChangeListener(this);
			multiPageCommandStackListener.removeCommandStack(
					getEditorForLayout(l).getEditorCommandStack());
			List<Layout> updatePages = new ArrayList<Layout>();
			for (Layout c : pagesMap.keySet()) {
				if (pagesMap.get(c) > pageIndex)
					updatePages.add(c);
			}
			for (Layout c : updatePages) {
				pagesMap.put(c, pagesMap.get(c) - 1);
			}
		}
		if (evt.getPropertyName().equals(Layout.NAME_PROP) &&
				evt.getSource() instanceof Layout) {
			Layout l = (Layout) evt.getSource();
			if (!pagesMap.containsKey(l))
				return;
			int pageIndex = pagesMap.get(l);
			setPageText(pageIndex, (String) evt.getNewValue());
		}
		if (evt.getPropertyName().equals(ModelElement.CHILD_MOVE_DOWN)) {
			if (evt.getNewValue() instanceof Layout) {
				Layout l = (Layout) evt.getNewValue();
				int pageIndex = pagesMap.get(l);
				pagesMap.remove(l);
				l.removePropertyChangeListener(this);
				multiPageCommandStackListener.removeCommandStack(
						getEditorForLayout(l).getEditorCommandStack());
				for (Layout c : pagesMap.keySet()) {
					if (pagesMap.get(c) == pageIndex + 1) {
						pagesMap.put(c, pageIndex);
						break;
					}
				}
				removePage(pageIndex);
				addPageForLayout(pageIndex + 1, l);
			}
		}
		if (evt.getPropertyName().equals(ModelElement.CHILD_MOVE_UP)) {
			if (evt.getNewValue() instanceof Layout) {
				Layout l = (Layout) evt.getNewValue();
				int pageIndex = pagesMap.get(l);
				pagesMap.remove(l);
				l.removePropertyChangeListener(this);
				multiPageCommandStackListener.removeCommandStack(
						getEditorForLayout(l).getEditorCommandStack());
				for (Layout c : pagesMap.keySet()) {
					if (pagesMap.get(c) == pageIndex - 1) {
						pagesMap.put(c, pageIndex);
						break;
					}
				}
				removePage(pageIndex);
				addPageForLayout(pageIndex - 1, l);
			}
		}
	}

    /**
     * This class listens for command stack changes of the pages
     * contained in this editor and decides if the editor is dirty or not.
     *  
     * @author Gunnar Wagenknecht
     */
    private class MultiPageCommandStackListener implements CommandStackListener {

        /** the observed command stacks */
        @SuppressWarnings("unchecked")
		private List commandStacks = new ArrayList(2);

        /**
         * Adds a <code>CommandStack</code> to observe.
         * @param commandStack
         */
        @SuppressWarnings("unchecked")
		public void addCommandStack(CommandStack commandStack) {
            commandStacks.add(commandStack);
            commandStack.addCommandStackListener(this);
        }
        
        public void removeCommandStack(CommandStack commandStack) {
        	commandStack.removeCommandStackListener(this);
        	commandStacks.remove(commandStack);
        }

        /* (non-Javadoc)
         * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
         */
        @SuppressWarnings("unchecked")
		public void commandStackChanged(EventObject event) {
            if (((CommandStack) event.getSource()).isDirty()) {
                // at least one command stack is dirty, 
                // so the multi page editor is dirty too
                setDirty(true);
            } else {
                // probably a save, we have to check all command stacks
                boolean oneIsDirty = false;
                for (Iterator stacks = commandStacks.iterator(); stacks.hasNext(); ) {
                    CommandStack stack = (CommandStack) stacks.next();
                    if (stack.isDirty()) {
                        oneIsDirty = true;
                        break;
                    }
                }
                setDirty(oneIsDirty);
            }
        }

        /**
         * Disposed the listener
         */
        @SuppressWarnings("unchecked")
		public void dispose() {
            for (Iterator stacks = commandStacks.iterator(); stacks.hasNext(); ) {
                ((CommandStack) stacks.next()).removeCommandStackListener(this);
            }
            commandStacks.clear();
        }

        /**
         * Marks every observed command stack beeing saved.
         * This method should be called whenever the editor/model
         * was saved.
         */
        @SuppressWarnings("unchecked")
		public void markSaveLocations() {
            for (Iterator stacks = commandStacks.iterator(); stacks.hasNext(); ) {
                CommandStack stack = (CommandStack) stacks.next();
                stack.markSaveLocation();
            }
        }
    }

    private List<IPageChangedListener> listeners = new ArrayList<IPageChangedListener>();

	@Override
	public void addPageChangedListener(IPageChangedListener listener) {
		listeners.add(listener);
	}

	@Override
	public Object getSelectedPage() {
		return getCurrentPage();
	}

	@Override
	public void removePageChangedListener(IPageChangedListener listener) {
		listeners.remove(listener);
	}

}
