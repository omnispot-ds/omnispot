package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class Deployment extends ModelElement {

	private static final long serialVersionUID = -2386076166432510134L;
	
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/alt_window_16.gif");

	/** Property ID to use when a layout is added to this deployment. */
	public static final String LAYOUT_ADDED_PROP = "Deployment.LayoutAdded";
	/** Property ID to use when a layout is removed from this deployment. */
	public static final String LAYOUT_REMOVED_PROP = "Deployment.LayoutRemoved";

	/* STATE */
	private List<Layout> layoutList;
	
	public Deployment() {
		layoutList = new ArrayList<Layout>();
	}
	
	/** 
	 * Add a layout to this deployment.
	 * @param s a non-null layout instance
	 * @return true, iff the layout was added, false otherwise
	 */
	public boolean addLayout(Layout s) {
		if (s != null && layoutList.add(s)) {
			firePropertyChange(LAYOUT_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/** Return a List of layouts in this deployment.  The returned List should not be modified. */
	public List<Layout> getLayouts() {
		return layoutList;
	}

	/**
	 * Remove a layout from this deployment.
	 * @param s a non-null layout instance;
	 * @return true, iff the layout was removed, false otherwise
	 */
	public boolean removeLayout(Layout s) {
		if (s != null && layoutList.remove(s)) {
			firePropertyChange(LAYOUT_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
}
