package com.kesdip.designer.editor;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.gef.ui.actions.MatchWidthAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

import com.kesdip.designer.action.CreateLayoutAction;
import com.kesdip.designer.action.DeleteLayoutAction;
import com.kesdip.designer.action.DesignerCopyAction;
import com.kesdip.designer.action.DesignerCutAction;
import com.kesdip.designer.action.DesignerPasteAction;
import com.kesdip.designer.action.MoveDownAction;
import com.kesdip.designer.action.MoveUpAction;
import com.kesdip.designer.action.MaximizeAction;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.LayoutRuler;
import com.kesdip.designer.parts.DesignerEditorEditPartFactory;
import com.kesdip.designer.parts.LayoutRulerProvider;

public class LayoutEditor extends GraphicalEditorWithFlyoutPalette {
	
	/** This is the root of the editor's model. */
	private Layout model;
	/** Palette component, holding the tools and shapes. */
	private static PaletteRoot PALETTE_MODEL;
	private DeploymentEditor parentEditor;
	private SelectionSynchronizer selectionSynchronizer;
	private TreeViewer outlineViewer;
	private ContextMenuProvider outlineContextMenuProvider;

	public LayoutEditor(DeploymentEditor parentEditor,
			SelectionSynchronizer selectionSynchronizer,
			TreeViewer outlineViewer) {
		setEditDomain(new DefaultEditDomain(this));
		this.parentEditor = parentEditor;
		this.selectionSynchronizer = selectionSynchronizer;
		this.outlineViewer = outlineViewer;
	}
	
	public Layout getModel() {
		return model;
	}
	
	public ContextMenuProvider getOutlineContextMenuProvider() {
		return outlineContextMenuProvider;
	}
	
	public CommandStack getEditorCommandStack() {
		return getCommandStack();
	}
	
	public DeploymentEditor getParentEditor() {
		return parentEditor;
	}
	
	public GraphicalViewer getViewer() {
		return getGraphicalViewer();
	}
	
	@Override
	public void dispose() {
		getSelectionSynchronizer().removeViewer(getGraphicalViewer());
		super.dispose();
	}

	@Override
	protected SelectionSynchronizer getSelectionSynchronizer() {
		return selectionSynchronizer;
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateActions(getSelectionActions());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		// Intentionally empty.
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void createActions() {
		super.createActions();
		
		ActionRegistry registry = getActionRegistry();
		IAction action;
		
		action = new DesignerCutAction(this, "Cut");
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new DesignerCopyAction(this, "Copy");
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new DesignerPasteAction(this, "Paste");
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new CreateLayoutAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new DeleteLayoutAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new MoveUpAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new MoveDownAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new MaximizeAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new MatchWidthAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new MatchHeightAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
		
		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		ActionRegistry parentRegistry = (ActionRegistry)
			getParentEditor().getAdapter(ActionRegistry.class);
		
		registry.registerAction(parentRegistry.getAction(GEFActionConstants.ZOOM_OUT));
		registry.registerAction(parentRegistry.getAction(GEFActionConstants.ZOOM_IN));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		// Intentionally empty
	}

	/**
	 * Configure the graphical viewer before it receives contents.
	 * <p>This is the place to choose an appropriate RootEditPart and EditPartFactory
	 * for your editor. The RootEditPart determines the behavior of the editor's "work-area".
	 * For example, GEF includes zoomable and scrollable root edit parts. The EditPartFactory
	 * maps model elements to edit parts (controllers).</p>
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new DesignerEditorEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
		
		IAction action = new ToggleSnapToGeometryAction(getGraphicalViewer());
		getActionRegistry().registerAction(action);

		action = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(action);
		
		action = getActionRegistry().getAction(GEFActionConstants.MATCH_HEIGHT);
		((MatchHeightAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.MATCH_WIDTH);
		((MatchWidthAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		action = getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP);
		((AlignmentAction) action).setSelectionProvider(getGraphicalViewer());
		
		LayoutRuler ruler = model.getRuler(PositionConstants.WEST);
		RulerProvider provider = null;
		if (ruler != null) {
			provider = new LayoutRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, provider);
		ruler = model.getRuler(PositionConstants.NORTH);
		provider = null;
		if (ruler != null) {
			provider = new LayoutRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, false);

		// Snap to Geometry property
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, 
				model.isSnapToGeometry());
		
		// Grid properties
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, 
				model.isShowGrid());
		// We keep grid visibility and enablement in sync
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, 
				model.isShowGrid());
	
		// configure the context menu provider
		ContextMenuProvider cmProvider =
				new DesignerEditorContentMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
		
		outlineContextMenuProvider = new DesignerEditorContentMenuProvider(
				outlineViewer, getActionRegistry());
	}

	public void saveProperties() {
		model.setShowGrid(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
		model.setSnapToGeometry(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(IEditorPart.PROP_DIRTY);
		super.commandStackChanged(event);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
	 */
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				// create a drag source listener for this palette viewer
				// together with an appropriate transfer drop target listener, this will enable
				// model element creation by dragging a CombinatedTemplateCreationEntries 
				// from the palette into the editor
				// @see ShapesEditor#createTransferDropTargetListener()
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}

	/**
	 * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry
	 * tool in the palette, this will enable model element creation by dragging from the palette.
	 * @see #createPaletteViewerProvider()
	 */
	private TransferDropTargetListener createTransferDropTargetListener() {
		return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
			@SuppressWarnings("unchecked")
			protected CreationFactory getFactory(Object template) {
				return new SimpleFactory((Class) template);
			}
		};
	}
	
	/**
	 * Set up the editor's inital content (after creation).
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(model); // set the contents of this editor
		
		// listen for dropped parts
		viewer.addDropTargetListener(createTransferDropTargetListener());
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof LayoutEditorInput) {
			if (model != ((LayoutEditorInput) input).getLayout())
				model = ((LayoutEditorInput) input).getLayout();
		}
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null)
			PALETTE_MODEL = DesignerEditorPaletteFactory.createPalette();
		return PALETTE_MODEL;
	}

}
