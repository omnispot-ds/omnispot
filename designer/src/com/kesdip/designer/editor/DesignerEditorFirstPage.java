package com.kesdip.designer.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.kesdip.designer.model.Root;
import com.kesdip.designer.parts.PageOneEditPartFactory;

public class DesignerEditorFirstPage extends EditorPart {
	private TreeViewer viewer;
	private DeploymentEditor containingEditor;
	private ActionRegistry actionRegistry;
	private CommandStack commandStack;
	
	public DesignerEditorFirstPage(DeploymentEditor containingEditor,
			ActionRegistry actionRegistry) {
		this.containingEditor = containingEditor;
		this.actionRegistry = actionRegistry;
		this.commandStack = new CommandStack();
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Handled by the multi page editor. Intentionally empty.
	}

	@Override
	public void doSaveAs() {
		// Handled by the multi page editor. Intentionally empty.
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return getCommandStack().isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == ActionRegistry.class) {
			return actionRegistry;
		} else if (adapter == CommandStack.class) {
			return commandStack;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer();
		viewer.createControl(parent);
		viewer.setEditPartFactory(new PageOneEditPartFactory());
		viewer.setEditDomain(new DefaultEditDomain(this));
		viewer.setContents(new Root(containingEditor.getModel()));
		
		ContextMenuProvider menuManager =
			new FirstPageContextMenuProvider(viewer, actionRegistry);
		menuManager.setRemoveAllWhenShown(true);
		viewer.setContextMenu(menuManager);
		getSite().registerContextMenu(menuManager, viewer);

		containingEditor.getSelectionSynchronizer().addViewer(viewer);
	}

	@Override
	public void dispose() {
		containingEditor.getSelectionSynchronizer().removeViewer(viewer);
		super.dispose();
	}

	@Override
	public void setFocus() {
		viewer.getControl().forceFocus();
	}

}
