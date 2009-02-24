package com.kesdip.designer.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.DeleteLayoutAction;
import com.kesdip.designer.handler.DeploymentEditorInput;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Root;
import com.kesdip.designer.parts.OutlinePartFactory;
import com.kesdip.designer.parts.PageOneEditPartFactory;

public class DeploymentEditor extends MultiPageEditorPart implements
		PropertyChangeListener {
	
	private Deployment d;
	private TreeViewer viewer;
	private Map<Layout, Integer> pagesMap;
	private SelectionSynchronizer synchronizer;
	private TreeViewer outlineViewer;
	private ContextMenuProvider pageOneContextMenuProvider;

	public DeploymentEditor() {
		// Intentionally empty
	}
	
	public Deployment getDeployment() {
		return d;
	}
	
	public void markSaveLocation() {
		// TODO Implement
	}
	
	public void markDirty() {
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IContentOutlinePage.class) {
			outlineViewer = new org.eclipse.gef.ui.parts.TreeViewer();
			getSite().setSelectionProvider(outlineViewer);
			return new ShapesOutlinePage(outlineViewer);
		}
		return super.getAdapter(adapter);
	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (outlineViewer == null)
			return;
		
		if (newPageIndex == 0) {
			outlineViewer.setContextMenu(pageOneContextMenuProvider);
			return;
		}
		LayoutEditor layoutEditor = (LayoutEditor) getEditor(newPageIndex);
		outlineViewer.setContextMenu(layoutEditor.getOutlineContextMenuProvider());
	}

	@Override
	protected void createPages() {
		viewer = new TreeViewer();
		viewer.createControl(getContainer());
		viewer.setEditDomain(new DefaultEditDomain(DeploymentEditor.this));
		viewer.setEditPartFactory(new PageOneEditPartFactory());
		addPage(viewer.getControl());
		setPageText(0, "Deployment");
		pagesMap = new HashMap<Layout, Integer>();
		getSelectionSynchronizer().addViewer(viewer);
		viewer.setContents(new Root(d));
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager menuMgr) {
				DeploymentEditor.this.fillContextMenu(menuMgr);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
		
		for (Layout l : d.getLayouts()) {
			addPageForLayout(l);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof DeploymentEditorInput) {
			d = ((DeploymentEditorInput) input).getDeployment();
			d.addPropertyChangeListener(this);
			String path = ((DeploymentEditorInput) input).getPath();
			if (path != null) {
				File f = new File(path);
				setPartName(f.getName());
			} else {
				setPartName("New Deployment");
			}
		}
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new CreateLayoutAction(d));
		if (viewer.getSelection() != null && !viewer.getSelection().isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			boolean selectionHasContainer = false;
			for (Object sel : selection.toList()) {
				if (sel instanceof Layout) {
					selectionHasContainer = true;
					break;
				}
			}
			if (selectionHasContainer)
				menuMgr.add(new DeleteLayoutAction(d, selection));
		}
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
	
	private void addPageForLayout(Layout l) {
		int count = pagesMap.size() + 1;
		try {
			addPage(new LayoutEditor(this, getSelectionSynchronizer(), outlineViewer),
					new LayoutEditorInput(l));
			pagesMap.put(l, count);
			setPageText(count, l.getName());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Deployment.LAYOUT_ADDED_PROP)) {
			addPageForLayout((Layout) evt.getNewValue());
		}
		if (evt.getPropertyName().equals(Deployment.LAYOUT_REMOVED_PROP)) {
			int pageIndex = pagesMap.get((Layout) evt.getNewValue());
			removePage(pageIndex);
			pagesMap.remove((Layout) evt.getNewValue());
			List<Layout> updatePages = new ArrayList<Layout>();
			for (Layout c : pagesMap.keySet()) {
				if (pagesMap.get(c) > pageIndex)
					updatePages.add(c);
			}
			for (Layout c : updatePages) {
				pagesMap.put(c, pagesMap.get(c) - 1);
			}
		}
	}

	/**
	 * Returns the selection syncronizer object. The synchronizer can be used to sync the
	 * selection of 2 or more EditPartViewers.
	 * @return the syncrhonizer
	 */
	protected SelectionSynchronizer getSelectionSynchronizer() {
		if (synchronizer == null)
			synchronizer = new SelectionSynchronizer();
		return synchronizer;
	}

	/**
	 * Creates an outline pagebook for this editor.
	 */
	public class ShapesOutlinePage extends ContentOutlinePage {	
		/**
		 * Create a new outline page for the shapes editor.
		 * @param viewer a viewer (TreeViewer instance) used for this outline page
		 * @throws IllegalArgumentException if editor is null
		 */
		public ShapesOutlinePage(EditPartViewer viewer) {
			super(viewer);
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			// create outline viewer page
			getViewer().createControl(parent);
			// configure outline viewer
			getViewer().setEditDomain(new DefaultEditDomain(DeploymentEditor.this));
			getViewer().setEditPartFactory(new OutlinePartFactory());
			// configure & add context menu to viewer
			pageOneContextMenuProvider = new DesignerEditorContentMenuProvider(
					getViewer(), null); 
			getViewer().setContextMenu(pageOneContextMenuProvider);
			getSite().registerContextMenu(
					"com.koutra.designer.editor.contextmenu",
					pageOneContextMenuProvider, getSite().getSelectionProvider());		
			// hook outline viewer
			getSelectionSynchronizer().addViewer(getViewer());
			// initialize outline viewer with model
			getViewer().setContents(new Root(d));
			// show outline viewer
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.ui.part.IPage#dispose()
		 */
		public void dispose() {
			// unhook outline viewer
			getSelectionSynchronizer().removeViewer(getViewer());
			// dispose
			super.dispose();
		}
	
		/* (non-Javadoc)
		 * @see org.eclipse.ui.part.IPage#getControl()
		 */
		public Control getControl() {
			return getViewer().getControl();
		}
		
		/**
		 * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
		 */
		public void init(IPageSite pageSite) {
			super.init(pageSite);
		}
	}

}
