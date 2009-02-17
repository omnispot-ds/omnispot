package com.kesdip.designer.view;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class DeploymentContentProvider implements IStructuredContentProvider {
	
	@Override
	public Object[] getElements(Object inputElement) {
		Deployment deployment = (Deployment) inputElement;
		List<Layout> layouts = deployment.getLayouts();
		Object[] retVal = new Object[layouts.size()];
		int idx = 0;
		for (Layout l : layouts) {
			retVal[idx++] = l;
		}
		
		return retVal;
	}

	@Override
	public void dispose() {
		// Intentionally left empty.
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Intentionally left empty.
	}

}
