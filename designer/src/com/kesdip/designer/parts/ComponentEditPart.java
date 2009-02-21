package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.FlashComponent;
import com.kesdip.designer.model.FlashWeatherComponent;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.VideoComponent;

public class ComponentEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart {

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
	protected IFigure createFigure() {
		IFigure f;
		if (getModel() instanceof ImageComponent)
			f = new RoundedRectangle();
		else if (getModel() instanceof VideoComponent)
			f = new Triangle();
		else if (getModel() instanceof TickerComponent)
			f = new Ellipse();
		else if (getModel() instanceof FlashComponent) {
			RoundedRectangle rr = new RoundedRectangle();
			rr.setCornerDimensions(new Dimension(20, 20));
			f = rr;
		} else if (getModel() instanceof FlashWeatherComponent) {
			RoundedRectangle rr = new RoundedRectangle();
			rr.setCornerDimensions(new Dimension(30, 30));
			f = rr;
		} else
			throw new RuntimeException("Unexpected model class: " +
					getModel().getClass().getName());
		
		f.setOpaque(true); // non-transparent figure
		f.setBackgroundColor(ColorConstants.green);
		return f;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ComponentModelElement.SIZE_PROP.equals(prop) || ComponentModelElement.LOCATION_PROP.equals(prop) ||
				ComponentModelElement.ZPOS_PROP.equals(prop)) {
			refreshVisuals();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
	}
	
	@Override
	protected void refreshVisuals() {
		// notify parent container of changed position & location
		// if this line is removed, the XYLayoutManager used by the parent container 
		// (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
		// and will not draw it correctly.
		Rectangle bounds = new Rectangle(((ComponentModelElement) getModel()).getLocation(),
				((ComponentModelElement) getModel()).getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart arg0) {
		return null;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request arg0) {
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart arg0) {
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request arg0) {
		return null;
	}
}
