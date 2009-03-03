package com.kesdip.designer;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM,
				0.6f, layout.getEditorArea());
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.LEFT,
				0.4f, layout.getEditorArea());
	}
}
