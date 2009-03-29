package com.kesdip.designer.editor;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Root;

public class DeploymentOutlinePage extends Page implements IContentOutlinePage {

	private EditPartViewer viewer;
	private Control outline;
	private SelectionSynchronizer selectionSynchronizer;
	private Deployment model;
	private ActionRegistry actionRegistry;
	private RootEditPart overviewContent;
	private PageBook pageBook;
	private Canvas overview;
	private LightweightSystem lws;
	private ScrollableThumbnail thumbnail;
	private IAction showOutlineAction;
	private IAction showOverviewAction;
	
	public DeploymentOutlinePage(EditPartViewer viewer,
			SelectionSynchronizer selectionSynchronizer, Deployment model,
			ActionRegistry actionRegistry) {
		super();
		this.viewer = viewer;
		this.selectionSynchronizer = selectionSynchronizer;
		this.model = model;
		this.actionRegistry = actionRegistry;
	}
	
	@Override
	public void setActionBars(IActionBars actionBars) {
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				actionRegistry.getAction(ActionFactory.UNDO.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				actionRegistry.getAction(ActionFactory.REDO.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(),
				actionRegistry.getAction(ActionFactory.CUT.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				actionRegistry.getAction(ActionFactory.COPY.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
				actionRegistry.getAction(ActionFactory.PASTE.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				actionRegistry.getAction(ActionFactory.DELETE.getId()));
		actionBars.updateActionBars();
	}

	@Override
	public void dispose() {
		selectionSynchronizer.removeViewer(getViewer());
		if (null != thumbnail)
            thumbnail.deactivate();
		super.dispose();
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getViewer().addSelectionChangedListener(listener);
	}

	/**
	 * Forwards the createControl request to the editpartviewer.
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		
		outline = getViewer().createControl(pageBook);
		getViewer().setContents(new Root(model));
		selectionSynchronizer.addViewer(getViewer());
		
		overview = new Canvas(pageBook, SWT.NONE);
		lws = new LightweightSystem(overview);
		
        IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
        showOutlineAction = new Action() {
            public void run() {
                showPage(outline);
            }
        };
        showOutlineAction.setImageDescriptor(ImageDescriptor.createFromFile(
        		DeploymentOutlinePage.class, "icons/outline.gif"));
        tbm.add(showOutlineAction);
        showOverviewAction = new Action() {
            public void run() {
                showPage(overview);
            }
        };
        showOverviewAction.setImageDescriptor(ImageDescriptor.createFromFile(
        		DeploymentOutlinePage.class, "icons/overview.gif"));
        tbm.add(showOverviewAction);
        
        initializeOverview();
        
        showPage(outline);
	}

    protected void showPage(Control id) {
        if (id == outline) {
            showOutlineAction.setChecked(true);
            showOverviewAction.setChecked(false);
            pageBook.showPage(outline);
            if (thumbnail != null)
                thumbnail.setVisible(false);
        }
        else if (id == overview) {
            showOutlineAction.setChecked(false);
            showOverviewAction.setChecked(true);
            pageBook.showPage(overview);
            if (thumbnail != null)
                thumbnail.setVisible(true);
        }
    }
    
    private void initializeOverview() {
        if(null == lws)
            return;
        
        if (null != thumbnail)
            thumbnail.deactivate();

        if (null != getOverviewContent()) {
            thumbnail = new ScrollableThumbnail();
            thumbnail.setBorder(new MarginBorder(3));
            lws.setContents(thumbnail);

            Viewport viewport = null;
            IFigure source = null;
            if (getOverviewContent() instanceof ScalableFreeformRootEditPart) {
                viewport = (Viewport)
                	((ScalableFreeformRootEditPart) getOverviewContent()).getFigure();
                source = ((ScalableFreeformRootEditPart) getOverviewContent()).getLayer(
                        LayerConstants.PRINTABLE_LAYERS);
            }
            if (getOverviewContent() instanceof ScalableRootEditPart) {
                viewport = (Viewport)
                	((ScalableRootEditPart) getOverviewContent()).getFigure();
                source = ((ScalableRootEditPart) getOverviewContent()).getLayer(
                		LayerConstants.PRINTABLE_LAYERS);
            }

            if (null != viewport && null != source) {
                thumbnail.setViewport(viewport);
                thumbnail.setSource(source);
            }
        }
    }

    public RootEditPart getOverviewContent() {
        return overviewContent;
    }

	public void setOverviewContent(RootEditPart part) {
        if (overviewContent != part) {
            overviewContent = part;
            initializeOverview();
        }
    }
	
    public void initialize(EditorPart newPage)
    {
        if (newPage instanceof LayoutEditor)
        {
        	LayoutEditor editorPage = (LayoutEditor) newPage;
            setOverviewContent(editorPage.getViewer().getRootEditPart());
        }
        else
        {
            setOverviewContent(null);
        }
    }

    
	/**
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	@Override
	public Control getControl() {
		return pageBook;
	}

	/**
	 * Forwards selection request to the viewer.
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		//$TODO when could this even happen?
		if (getViewer() == null)
			return StructuredSelection.EMPTY;
		return getViewer().getSelection();
	}

	/**
	 * Returns the EditPartViewer
	 * @return the viewer
	 */
	protected EditPartViewer getViewer() {
		return viewer;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		getViewer().removeSelectionChangedListener(listener);
	}

	/**
	 * Sets focus to a part in the page.
	 */
	@Override
	public void setFocus() {
		if (getControl() != null)
			getControl().setFocus();
	}

	/**
	 * @see ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (getViewer() != null)
			getViewer().setSelection(selection);
	}
}
