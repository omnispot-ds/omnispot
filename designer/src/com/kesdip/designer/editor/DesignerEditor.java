package com.kesdip.designer.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.kesdip.designer.action.DesignerCopyAction;
import com.kesdip.designer.action.DesignerCutAction;
import com.kesdip.designer.action.DesignerPasteAction;
import com.kesdip.designer.handler.LayoutEditorInput;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.parts.DesignerEditorEditPartFactory;
import com.kesdip.designer.parts.OutlinePartFactory;
import com.kesdip.designer.utils.DesignerLog;

public class DesignerEditor extends GraphicalEditorWithFlyoutPalette {
	
	/** This is the root of the editor's model. */
	private Layout model;
	/** Palette component, holding the tools and shapes. */
	private static PaletteRoot PALETTE_MODEL;


	public DesignerEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}


	private void createOutputStream(OutputStream os) throws IOException {
		// TODO This should serialize to XML : the Deployment Descriptor.
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(model);
		oos.close();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(
				new ByteArrayInputStream(out.toByteArray()), 
				true,  // keep saving, even if IFile is out of sync with the Workspace
				false, // don't keep history
				monitor); // progress monitor
			getCommandStack().markSaveLocation();
		} catch (CoreException ce) { 
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void markSaveLocation() {
		getCommandStack().markSaveLocation();
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
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		// Show a SaveAs dialog
		Shell shell = getSite().getWorkbenchWindow().getShell();
		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
		dialog.open();
		
		IPath path = dialog.getResult();	
		if (path != null) {
			// try to save the editor's contents under a different file name
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			try {
				new ProgressMonitorDialog(shell).run(
						false, // don't fork
						false, // not cancelable
						new WorkspaceModifyOperation() { // run this operation
							public void execute(final IProgressMonitor monitor) {
								try {
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									createOutputStream(out);
									file.create(
										new ByteArrayInputStream(out.toByteArray()), // contents
										true, // keep saving, even if IFile is out of sync with the Workspace
										monitor); // progress monitor
								} catch (CoreException ce) {
									ce.printStackTrace();
								} catch (IOException ioe) {
									ioe.printStackTrace();
								} 
							}
						});
				// set input to the new file
				setInput(new FileEditorInput(file));
				getCommandStack().markSaveLocation();
			} catch (InterruptedException ie) {
	  			// should not happen, since the monitor dialog is not cancelable
				ie.printStackTrace(); 
			} catch (InvocationTargetException ite) { 
				ite.printStackTrace(); 
			}
		}
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class type) {
		if (type == IContentOutlinePage.class)
			return new ShapesOutlinePage(new TreeViewer());
		return super.getAdapter(type);
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


	private void handleLoadException(Exception e) {
		DesignerLog.logError("Unable to load model.", e);
		model = new Layout();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		try {
			// TODO: This should de-serialize XML (i.e. the deployment descriptor).
			if (input instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) input).getFile();
				ObjectInputStream in = new ObjectInputStream(file.getContents());
				model = (Layout) in.readObject();
				in.close();
				setPartName(file.getName());
			} else if (input instanceof LayoutEditorInput) {
				model = ((LayoutEditorInput) input).getLayout();
				setPartName(model.getName());
			}
		} catch (IOException e) { 
			handleLoadException(e); 
		} catch (CoreException e) { 
			handleLoadException(e); 
		} catch (ClassNotFoundException e) { 
			handleLoadException(e); 
		}
	}


	@Override
	protected PaletteRoot getPaletteRoot() {
		if (PALETTE_MODEL == null)
			PALETTE_MODEL = DesignerEditorPaletteFactory.createPalette();
		return PALETTE_MODEL;
	}

	/************************************************************************************/
	
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
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new OutlinePartFactory());
			// configure & add context menu to viewer
			ContextMenuProvider cmProvider = new DesignerEditorContentMenuProvider(
					getViewer(), getActionRegistry()); 
			getViewer().setContextMenu(cmProvider);
			getSite().registerContextMenu(
					"com.koutra.designer.editor.contextmenu",
					cmProvider, getSite().getSelectionProvider());		
			// hook outline viewer
			getSelectionSynchronizer().addViewer(getViewer());
			// initialize outline viewer with model
			getViewer().setContents(model);
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
			ActionRegistry registry = getActionRegistry();
			IActionBars bars = pageSite.getActionBars();
			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
		}
	}

}
