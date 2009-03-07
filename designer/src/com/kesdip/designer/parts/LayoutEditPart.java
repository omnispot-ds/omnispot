package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.kesdip.designer.command.RegionConstraintChange;
import com.kesdip.designer.command.RegionCreation;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.utils.DesignerLog;

public class LayoutEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener {

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
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());

		// Create the static router for the connection layer
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
		
		return f;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		// handles constraint changes (e.g. moving and/or resizing) of model elements
		// and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE,  new RegionXYLayoutEditPolicy());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelChildren() {
		return ((Layout) getModel()).getChildren(); // return a list of shapes
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		// these properties are fired when Shapes are added into or removed from 
		// the ShapeDiagram instance and must cause a call of refreshChildren()
		// to update the diagram's contents.
		if (Layout.REGION_ADDED_PROP.equals(prop)
				|| Layout.REGION_REMOVED_PROP.equals(prop)
				|| Layout.CHILD_MOVE_UP.equals(prop)
				|| Layout.CHILD_MOVE_DOWN.equals(prop)) {
			refreshChildren();
		}
	}

	/**
	 * EditPolicy for the Figure used by this edit part.
	 * Children of XYLayoutEditPolicy can be used in Figures with XYLayout.
	 * @author Elias Volanakis
	 */
	private static class RegionXYLayoutEditPolicy extends XYLayoutEditPolicy {
		
		/* (non-Javadoc)
		 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(ChangeBoundsRequest, EditPart, Object)
		 */
		protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
				EditPart child, Object constraint) {
			if (child instanceof RegionEditPart && constraint instanceof Rectangle) {
				// return a command that can move and/or resize a Shape
				Layout layout = (Layout) getHost().getModel();
				Deployment deployment = (Deployment) layout.getParent();
				Rectangle bounds = (Rectangle) constraint;
				bounds = bounds.intersect(new Rectangle(
						new Point(0, 0), deployment.getSize()));
				if (!bounds.isEmpty())
					return new RegionConstraintChange(
							(Region) child.getModel(), request, bounds);
			}
			return super.createChangeConstraintCommand(request, child, constraint);
		}
		
		/* (non-Javadoc)
		 * @see ConstrainedLayoutEditPolicy#createChangeConstraintCommand(EditPart, Object)
		 */
		protected Command createChangeConstraintCommand(EditPart child,
				Object constraint) {
			// not used in this example
			return null;
		}
		
		/* (non-Javadoc)
		 * @see LayoutEditPolicy#getCreateCommand(CreateRequest)
		 */
		protected Command getCreateCommand(CreateRequest request) {
			Object childClass = request.getNewObjectType();
			if (childClass == Region.class) {
				Region element = null;
				try {
					element = (Region) request.getNewObject();
				} catch (Error e) {
					DesignerLog.logError("New Object could not be cast to a Region", e);
					throw e;
				}
				Rectangle bounds = (Rectangle) getConstraintFor(request);
				if (bounds.getSize().isEmpty())
					bounds.setSize(element.getSize());
				Layout layout = (Layout) getHost().getModel();
				Deployment deployment = (Deployment) layout.getParent();
				bounds = bounds.intersect(new Rectangle(
						new Point(0, 0), deployment.getSize()));
				if (bounds.isEmpty())
					return null;
				return new RegionCreation(element, layout, bounds);
			}
			return null;
		}
		
	}
}
