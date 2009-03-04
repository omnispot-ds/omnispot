package com.kesdip.designer.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
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
import com.kesdip.designer.handler.DeploymentEditorInput;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.parts.OutlinePartFactory;

@SuppressWarnings("restriction")
public class DeploymentEditor extends MultiPageEditorPart implements
		PropertyChangeListener, ISelectionChangedListener, CommandStackListener {
	
	private Deployment model;
	private TreeViewer outlineViewer;
	private SelectionSynchronizer synchronizer;
	private ActionRegistry actionRegistry;
	private MultiPageCommandStackListener multiPageCommandStackListener;
	private DelegatingCommandStack delegatingCommandStack;
    private CommandStackListener delegatingCommandStackListener;
    private ISelectionListener selectionListener;
	private boolean isDirty;
	private Map<Layout, Integer> pagesMap;
	private Map<Layout, LayoutEditor> pageEditorsMap;


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

    /**
     * Indicates that the current page has changed.
     * <p>
     * We update the DelegatingCommandStack, OutlineViewer
     * and other things here.
     */
    protected void currentPageChanged()
    {
        // update delegating command stack
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

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == ActionRegistry.class) {
			return actionRegistry;
		} else if (adapter == CommandStack.class) {
			return getDelegatingCommandStack();
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
			return new OutlinePage(outlineViewer, getSelectionSynchronizer(), model);
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

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Implement

	}

	@Override
	public void doSaveAs() {
		// TODO Implement

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

}
