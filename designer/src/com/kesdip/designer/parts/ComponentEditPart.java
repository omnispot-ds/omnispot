package com.kesdip.designer.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.kesdip.designer.editor.DesignerComponentEditPolicy;
import com.kesdip.designer.figure.ClockFigure;
import com.kesdip.designer.figure.FlashFigure;
import com.kesdip.designer.figure.FlashWeatherFigure;
import com.kesdip.designer.figure.ImageFigure;
import com.kesdip.designer.figure.TickerFigure;
import com.kesdip.designer.figure.TunerVideoFigure;
import com.kesdip.designer.figure.VideoFigure;
import com.kesdip.designer.model.ClockComponent;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.FlashComponent;
import com.kesdip.designer.model.FlashWeatherComponent;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.TunerVideoComponent;
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
		if (getModel() instanceof ImageComponent) {
			ImageComponent imageComponent = (ImageComponent) getModel();
			f = new ImageFigure(imageComponent);
		} else if (getModel() instanceof ClockComponent) {
			ClockComponent clockComponent = (ClockComponent) getModel();
			f = new ClockFigure(clockComponent);
		} else if (getModel() instanceof VideoComponent) {
			VideoComponent videoComponent = (VideoComponent) getModel();
			f = new VideoFigure(videoComponent);
		} else if (getModel() instanceof TunerVideoComponent) {
			TunerVideoComponent tunerVideoComponent = (TunerVideoComponent) getModel();
			f = new TunerVideoFigure(tunerVideoComponent);
		} else if (getModel() instanceof TickerComponent) {
			TickerComponent tickerComponent = (TickerComponent) getModel();
			f = new TickerFigure(tickerComponent);
		} else if (getModel() instanceof FlashComponent) {
			FlashComponent flashComponent = (FlashComponent) getModel();
			f = new FlashFigure(flashComponent);
		} else if (getModel() instanceof FlashWeatherComponent) {
			FlashWeatherComponent flashWeatherComponent = (FlashWeatherComponent) getModel();
			f = new FlashWeatherFigure(flashWeatherComponent);
		} else
			throw new RuntimeException("Unexpected model class: " +
					getModel().getClass().getName());
		
		return f;
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ComponentModelElement.SIZE_PROP.equals(prop) ||
				ComponentModelElement.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new DesignerComponentEditPolicy());
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
