package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.ModelElement;

public class OutlineComponentPart extends AbstractTreeEditPart implements
		PropertyChangeListener {
	
	public OutlineComponentPart(ComponentModelElement model) {
		super(model);
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DesignerComponentEditPolicy());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	protected Image getImage() {
		return ((ComponentModelElement) getModel()).getIcon();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
	 */
	protected String getText() {
		return ((ComponentModelElement) getModel()).toString();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

}
