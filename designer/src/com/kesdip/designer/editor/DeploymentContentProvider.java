package com.kesdip.designer.editor;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;

public class DeploymentContentProvider implements ITreeContentProvider {
	private Object ROOT;
	private Deployment d;
	
	public DeploymentContentProvider(Deployment d) {
		this.ROOT = new Object();
		this.d = d;
	}
	
	public Object getRoot() {
		return ROOT;
	}

	@Override
	public void dispose() {
		// Intentionally left empty.
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Intentionally left empty.
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement.equals(ROOT)) {
			Object[] retVal = new Object[1];
			retVal[0] = d;
			return retVal;
		}
		
		if (parentElement instanceof Layout) {
			Object[] retVal = new Object[0];
			return retVal;
		}
		
		Deployment i = (Deployment) parentElement;
		List<ModelElement> li = i.getChildren();
		Object[] retVal = new Object[li.size()];
		int count = 0;
		for (ModelElement c : li) {
			retVal[count++] = c;
		}
		return retVal;
	}

	@Override
	public Object getParent(Object element) {
		if (element.equals(ROOT))
			return null;
		if (element instanceof Deployment)
			return ROOT;
		return d;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element.equals(ROOT))
			return true;
		if (element instanceof Layout)
			return false;
		Deployment i = (Deployment) element;
		return !i.getChildren().isEmpty();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

}
