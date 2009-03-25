package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class CronDatumContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		CronDatum datum = (CronDatum) inputElement;
		return datum.getCronData().toArray(new Integer[datum.getCronData().size()]);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//System.out.println("input changed: " + oldInput + ", " + newInput);
	}

}
