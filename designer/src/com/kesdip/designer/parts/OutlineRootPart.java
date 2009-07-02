package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.model.Root;

public class OutlineRootPart extends AbstractTreeEditPart implements
		PropertyChangeListener {
	public OutlineRootPart(Root root) {
		super(root);
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
		}
	}

	@Override
	protected void createEditPolicies() {
		// If this editpart is the root content of the viewer, then disallow removal
		if (getParent() instanceof RootEditPart) {
			installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		List retVal = new ArrayList();
		retVal.add(((Root) getModel()).getDeployment());
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
	 */
	protected Image getImage() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
	 */
	protected String getText() {
		return "NOT_USED";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

}
