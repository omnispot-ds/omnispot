package com.kesdip.designer.handler;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.kesdip.designer.model.Layout;

public class LayoutEditorInput extends PlatformObject implements IEditorInput {
	private Layout layout;
	
	public LayoutEditorInput(Layout layout) {
		this.layout = layout;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	@Override
	public String getName() {
		return layout.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return layout.toString();
	}
	
	public Layout getLayout() {
		return layout;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof LayoutEditorInput))
			return false;
		
		LayoutEditorInput lei = (LayoutEditorInput) other;
		return layout.equals(lei.layout);
	}

}
