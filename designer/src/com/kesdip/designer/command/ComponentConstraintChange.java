package com.kesdip.designer.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import com.kesdip.designer.model.ComponentModelElement;

public class ComponentConstraintChange extends Command {

	/** Stores the new size and location. */
	private final Rectangle newBounds;
	/** Stores the old size and location. */
	private Rectangle oldBounds;
	/** A request to move/resize an edit part. */
	private final ChangeBoundsRequest request;
	/** Element to manipulate. */
	private final ComponentModelElement element;
		
	/**
	 * Create a command that can resize and/or move a shape. 
	 * @param shape	the shape to manipulate
	 * @param req		the move and resize request
	 * @param newBounds the new size and location
	 * @throws IllegalArgumentException if any of the parameters is null
	 */
	public ComponentConstraintChange(ComponentModelElement element, ChangeBoundsRequest req, 
			Rectangle newBounds) {
		if (element == null || req == null || newBounds == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move/resize");
		this.element = element;
		this.request = req;
		this.newBounds = newBounds.getCopy();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		Object type = request.getType();
		// make sure the Request is of a type we support:
		return (RequestConstants.REQ_MOVE.equals(type)
				|| RequestConstants.REQ_MOVE_CHILDREN.equals(type) 
				|| RequestConstants.REQ_RESIZE.equals(type)
				|| RequestConstants.REQ_RESIZE_CHILDREN.equals(type));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		oldBounds = new Rectangle(element.getLocation(), element.getSize());
		redo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		element.setSize(newBounds.getSize());
		element.setLocation(newBounds.getLocation());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		element.setSize(oldBounds.getSize());
		element.setLocation(oldBounds.getLocation());
	}
}
