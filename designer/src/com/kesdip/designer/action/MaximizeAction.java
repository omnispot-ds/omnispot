package com.kesdip.designer.action;

import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IEditorPart;

import com.kesdip.designer.command.ComponentConstraintChange;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.properties.DimensionPropertySource;
import com.kesdip.designer.properties.LocationPropertySource;

public class MaximizeAction extends SelectionAction {
	public static final String ID = "com.kesdip.designer.action.maximize";

	public MaximizeAction(IEditorPart editor) {
		super(editor);
		setId(ID);
		setLazyEnablementCalculation(false);
	}

	@Override
	public void run() {
		execute(createComponentConstraintChangeCommand(getSelectedObjects()));
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createComponentConstraintChangeCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}
	
	@SuppressWarnings("unchecked")
	private Command createComponentConstraintChangeCommand(List selection) {
		if (selection == null || selection.isEmpty() || selection.size() != 1)
			return null;
		if (!(selection.get(0) instanceof EditPart))
			return null;
		EditPart editPart = (EditPart) selection.get(0);
		if (!(editPart.getModel() instanceof ComponentModelElement))
			return null;
		ComponentModelElement element = (ComponentModelElement) editPart.getModel();
		ModelElement parent = element.getParent();
		if (parent == null
				 || parent instanceof Deployment)
			return null;
		
		Rectangle newBounds;
		if (parent instanceof Layout) {
			Deployment deployment = (Deployment) parent.getParent();
			newBounds = new Rectangle(
					new Point(0, 0),
					(Dimension) ((DimensionPropertySource) deployment.getPropertyValue(
							Deployment.SIZE_PROP)).getEditableValue());
		} else {
			newBounds = new Rectangle(
					(Point) ((LocationPropertySource) parent.getPropertyValue(
							ComponentModelElement.LOCATION_PROP)).getEditableValue(),
					(Dimension) ((DimensionPropertySource) parent.getPropertyValue(
							ComponentModelElement.SIZE_PROP)).getEditableValue());
		}
		Command retVal = new ComponentConstraintChange(element, newBounds);
		return retVal;
	}

	/**
	 * Initializes this action's text and images.
	 */
	protected void init() {
		super.init();
		setText("Maximize");
		setToolTipText("Set the size of this component to its container's size");
		setId(ID);
		setEnabled(false);
	}
}
