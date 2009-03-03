package com.kesdip.designer.editor;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
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
import com.kesdip.designer.action.LayoutMoveDownAction;
import com.kesdip.designer.action.LayoutMoveUpAction;
import com.kesdip.designer.action.MaximizeAction;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.parts.DesignerEditorEditPartFactory;

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
	
	public ContextMenuProvider getOutlineContextMenuProvider() {
		return outlineContextMenuProvider;
	}
	
	public CommandStack getEditorCommandStack() {
		return getCommandStack();
	}
	
	public DeploymentEditor getParentEditor() {
		return parentEditor;
	}
	
	@Override
	protected SelectionSynchronizer getSelectionSynchronizer() {
		return selectionSynchronizer;
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		updateActions(getSelectionActions());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class type) {
		return super.getAdapter(type);
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

		action = new LayoutMoveUpAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new LayoutMoveDownAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);

		action = new MaximizeAction(this);
		getSelectionActions().add(action.getId());
		registry.registerAction(action);
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
	
		// configure the context menu provider
		ContextMenuProvider cmProvider =
				new DesignerEditorContentMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
		
		outlineContextMenuProvider = new DesignerEditorContentMenuProvider(
				outlineViewer, getActionRegistry());
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
