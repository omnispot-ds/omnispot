package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;

public class OutlineRegionPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

	public OutlineRegionPart(Region model) {
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

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		return ((Region) getModel()).getChildren();
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
		String prop = evt.getPropertyName();
		if (Region.COMPONENT_ADDED_PROP.equals(prop) ||
				Region.COMPONENT_REMOVED_PROP.equals(prop) ||
				Region.CHILD_MOVE_UP.equals(prop) ||
				Region.CHILD_MOVE_DOWN.equals(prop)) {
			refreshChildren();
		} else {
			refreshVisuals();
		}
	}

}
