package com.kesdip.designer.editor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class DeploymentLabelProvider extends LabelProvider implements
		ILabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof Deployment) {
			Deployment c = (Deployment) element;
			return c.getIcon();
		} else {
			Layout c = (Layout) element;
			return c.getIcon();
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Deployment) {
			Deployment c = (Deployment) element;
			return c.toString();
		} else {
			Layout c = (Layout) element;
			return c.getName();
		}
	}
}
