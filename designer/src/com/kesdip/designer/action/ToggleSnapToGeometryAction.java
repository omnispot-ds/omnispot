package com.kesdip.designer.action;

import org.eclipse.gef.ui.parts.GraphicalViewerImpl;

public class ToggleSnapToGeometryAction extends
		org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction {
	public ToggleSnapToGeometryAction() {
		super(new GraphicalViewerImpl());
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}
}
