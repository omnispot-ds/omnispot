package com.kesdip.designer.properties;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kesdip.designer.model.Resource;

public class ResourceListContentProvider implements IStructuredContentProvider {
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Resource> resourceList = (List<Resource>) inputElement;
		Object[] retVal = new Object[resourceList.size()];
		int idx = 0;
		for (Resource r : resourceList) {
			retVal[idx++] = r;
		}
		
		return retVal;
	}

	@Override
	public void dispose() {
		// Intentionally empty
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Intentionally empty
	}

}
