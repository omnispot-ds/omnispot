package com.kesdip.designer.action;

import org.eclipse.gef.ui.parts.GraphicalViewerImpl;

public class ToggleGridAction extends
		org.eclipse.gef.ui.actions.ToggleGridAction {
	public ToggleGridAction() {
		super(new GraphicalViewerImpl());
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}
}
