package com.kesdip.designer.editor;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Root;

public class OutlinePage extends ContentOutlinePage {
	private SelectionSynchronizer selectionSynchronizer;
	private Deployment model;

	public OutlinePage(EditPartViewer viewer,
			SelectionSynchronizer selectionSynchronizer, Deployment model) {
		super(viewer);
		this.selectionSynchronizer = selectionSynchronizer;
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		getViewer().createControl(parent);
		getViewer().setContents(new Root(model));
		selectionSynchronizer.addViewer(getViewer());
	}

	@Override
	public Control getControl() {
		return getViewer().getControl();
	}

	@Override
	public void dispose() {
		selectionSynchronizer.removeViewer(getViewer());
		super.dispose();
	}

}
