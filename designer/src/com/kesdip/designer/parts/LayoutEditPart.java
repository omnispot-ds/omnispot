package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;

import com.kesdip.designer.command.ChangeGuideCommand;
import com.kesdip.designer.command.CloneCommand;
import com.kesdip.designer.command.RegionConstraintChange;
import com.kesdip.designer.command.RegionCreation;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.LayoutGuide;
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
		
		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy());
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
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List<SnapToHelper> snapStrategies = new ArrayList<SnapToHelper>();
			Boolean val = (Boolean) getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGeometry(this));
			val = (Boolean) getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGrid(this));
			
			if (snapStrategies.size() == 0)
				return null;
			if (snapStrategies.size() == 1)
				return snapStrategies.get(0);

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++)
				ss[i] = snapStrategies.get(i);
			return new CompoundSnapToHelper(snapStrategies.toArray(
					new SnapToHelper[snapStrategies.size()]));
		}
		return super.getAdapter(adapter);
	}

	/**
	 * EditPolicy for the Figure used by this edit part.
	 * Children of XYLayoutEditPolicy can be used in Figures with XYLayout.
	 * @author Elias Volanakis
	 */
	private static class RegionXYLayoutEditPolicy extends XYLayoutEditPolicy {
		
		protected Command chainGuideAttachmentCommand(Request request,
				ComponentModelElement part, Command cmd, boolean horizontal) {
			Command result = cmd;
			
			// Attach to guide, if one is given
			Integer guidePos = (Integer)request.getExtendedData()
					.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
					                : SnapToGuides.KEY_VERTICAL_GUIDE);
			if (guidePos != null) {
				int alignment = ((Integer)request.getExtendedData()
						.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
						                : SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
				ChangeGuideCommand cgm = new ChangeGuideCommand(part, horizontal);
				cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal), alignment);
				result = result.chain(cgm);
			}

			return result;
		}

		protected Command chainGuideDetachmentCommand(Request request,
				ComponentModelElement part, Command cmd, boolean horizontal) {
			Command result = cmd;
			
			// Detach from guide, if none is given
			Integer guidePos = (Integer)request.getExtendedData()
					.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
					                : SnapToGuides.KEY_VERTICAL_GUIDE);
			if (guidePos == null)
				result = result.chain(new ChangeGuideCommand(part, horizontal));

			return result;
		}

		protected LayoutGuide findGuideAt(int pos, boolean horizontal) {
			RulerProvider provider = ((RulerProvider)getHost().getViewer().getProperty(
					horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER 
					: RulerProvider.PROPERTY_HORIZONTAL_RULER));
			return (LayoutGuide)provider.getGuideAt(pos);
		}

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
				Region part = (Region) child.getModel();
				if (!bounds.isEmpty()) {
					Command result = new RegionConstraintChange(part, bounds);

					if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) {
						Integer guidePos = (Integer)request.getExtendedData()
								.get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
						if (guidePos != null) {
							result = chainGuideAttachmentCommand(request, part, result, true);
						} else if (part.getHorizontalGuide() != null) {
							// SnapToGuides didn't provide a horizontal guide, but this part is attached
							// to a horizontal guide.  Now we check to see if the part is attached to
							// the guide along the edge being resized.  If that is the case, we need to
							// detach the part from the guide; otherwise, we leave it alone.
							int alignment = part.getHorizontalGuide().getAlignment(part);
							int edgeBeingResized = 0;
							if ((request.getResizeDirection() & PositionConstants.NORTH) != 0)
								edgeBeingResized = -1;
							else
								edgeBeingResized = 1;
							if (alignment == edgeBeingResized)
								result = result.chain(new ChangeGuideCommand(part, true));
						}
					}
					
					if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) {
						Integer guidePos = (Integer)request.getExtendedData()
								.get(SnapToGuides.KEY_VERTICAL_GUIDE);
						if (guidePos != null) {
							result = chainGuideAttachmentCommand(request, part, result, false);
						} else if (part.getVerticalGuide() != null) {
							int alignment = part.getVerticalGuide().getAlignment(part);
							int edgeBeingResized = 0;
							if ((request.getResizeDirection() & PositionConstants.WEST) != 0)
								edgeBeingResized = -1;
							else
								edgeBeingResized = 1;
							if (alignment == edgeBeingResized)
								result = result.chain(new ChangeGuideCommand(part, false));
						}
					}
					
					if (request.getType().equals(REQ_MOVE_CHILDREN)
							|| request.getType().equals(REQ_ALIGN_CHILDREN)) {
						result = chainGuideAttachmentCommand(request, part, result, true);
						result = chainGuideAttachmentCommand(request, part, result, false);
						result = chainGuideDetachmentCommand(request, part, result, true);
						result = chainGuideDetachmentCommand(request, part, result, false);
					}

					return result;
				}
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
				Command cmd = new RegionCreation(element, layout, bounds);
				
				cmd = chainGuideAttachmentCommand(request, element, cmd, true);
				return chainGuideAttachmentCommand(request, element, cmd, false);
			}
			return null;
		}
		
		protected Command createAddCommand(Request request, EditPart childEditPart, 
				Object constraint) {
			Region part = (Region) childEditPart.getModel();
			Rectangle rect = (Rectangle)constraint;

			if (!(getHost().getModel() instanceof Layout))
				return null;
			
			Layout layout = (Layout) getHost().getModel();
			RegionCreation add = new RegionCreation(part, layout, rect);
			add.setLabel("Add Command");

			RegionConstraintChange setConstraint =
				new RegionConstraintChange(part, rect);
			setConstraint.setLabel("Set Constraint Command");
			
			Command cmd = add.chain(setConstraint);
			cmd = chainGuideAttachmentCommand(request, part, cmd, true);
			cmd = chainGuideAttachmentCommand(request, part, cmd, false);
			cmd = chainGuideDetachmentCommand(request, part, cmd, true);
			return chainGuideDetachmentCommand(request, part, cmd, false);
		}

		@SuppressWarnings("unchecked")
		protected Command getAddCommand(Request generic) {
			ChangeBoundsRequest request = (ChangeBoundsRequest)generic;
			List editParts = request.getEditParts();
			CompoundCommand command = new CompoundCommand();
			command.setDebugLabel("Add in ConstrainedLayoutEditPolicy");//$NON-NLS-1$
			GraphicalEditPart childPart;
			Rectangle r;
			Object constraint;

			for (int i = 0; i < editParts.size(); i++) {
				childPart = (GraphicalEditPart)editParts.get(i);
				r = childPart.getFigure().getBounds().getCopy();
				//convert r to absolute from childpart figure
				childPart.getFigure().translateToAbsolute(r);
				r = request.getTransformedRectangle(r);
				//convert this figure to relative 
				getLayoutContainer().translateToRelative(r);
				getLayoutContainer().translateFromParent(r);
				r.translate(getLayoutOrigin().getNegated());
				constraint = getConstraintFor(r);
				command.add(createAddCommand(generic, childPart,
					translateToModelConstraint(constraint)));
			}
			return command.unwrap();
		}

		/**
		 * Override to return the <code>Command</code> to perform an {@link
		 * RequestConstants#REQ_CLONE CLONE}. By default, <code>null</code> is
		 * returned.
		 * @param request the Clone Request
		 * @return A command to perform the Clone.
		 */
		@SuppressWarnings("unchecked")
		protected Command getCloneCommand(ChangeBoundsRequest request) {
			CloneCommand clone = new CloneCommand();
			
			clone.setParent((Layout) getHost().getModel());
			
			Iterator i = request.getEditParts().iterator();
			GraphicalEditPart currPart = null;
			
			while (i.hasNext()) {
				currPart = (GraphicalEditPart)i.next();
				clone.addPart((ComponentModelElement) currPart.getModel(), 
						(Rectangle) getConstraintForClone(currPart, request));
			}
			
			// Attach to horizontal guide, if one is given
			Integer guidePos = (Integer) request.getExtendedData()
					.get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
			if (guidePos != null) {
				int hAlignment = ((Integer)request.getExtendedData()
						.get(SnapToGuides.KEY_HORIZONTAL_ANCHOR)).intValue();
				clone.setGuide(findGuideAt(guidePos.intValue(), true), hAlignment, true);
			}
			
			// Attach to vertical guide, if one is given
			guidePos = (Integer)request.getExtendedData()
					.get(SnapToGuides.KEY_VERTICAL_GUIDE);
			if (guidePos != null) {
				int vAlignment = ((Integer)request.getExtendedData()
						.get(SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
				clone.setGuide(findGuideAt(guidePos.intValue(), false), vAlignment, false);
			}

			return clone;
		}

		/**
		 * Returns the layer used for displaying feedback.
		 *  
		 * @return the feedback layer
		 */
		protected IFigure getFeedbackLayer() {
			return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
		}
			
	}
}
