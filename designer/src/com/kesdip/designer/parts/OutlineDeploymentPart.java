package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.ModelElement;

public class OutlineDeploymentPart extends AbstractTreeEditPart implements
		PropertyChangeListener {
	
	public OutlineDeploymentPart(Deployment model) {
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
		return ((Deployment) getModel()).getLayouts();
	}

	/**
	 * Convenience method that returns the EditPart corresponding to a given child.
	 * @param child a model element instance
	 * @return the corresponding EditPart or null
	 */
	private EditPart getEditPartForChild(Object child) {
		return (EditPart) getViewer().getEditPartRegistry().get(child);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	protected Image getImage() {
		return ((Deployment) getModel()).getIcon();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
	 */
	protected String getText() {
		return ((Deployment) getModel()).toString();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Deployment.LAYOUT_ADDED_PROP.equals(prop)) {
			// add a child to this edit part
			// causes an additional entry to appear in the tree of the outline view
			addChild(createChild(evt.getNewValue()), -1);
		} else if (Deployment.LAYOUT_REMOVED_PROP.equals(prop)) {
			// remove a child from this edit part
			// causes the corresponding edit part to disappear from the tree in the outline view
			removeChild(getEditPartForChild(evt.getNewValue()));
		} else {
			refreshVisuals();
		}
	}

}
