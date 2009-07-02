package com.kesdip.designer.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.Region;

public class RegionConstraintChange extends Command {

	/** Stores the new size and location. */
	private final Rectangle newBounds;
	/** Stores the old size and location. */
	private Rectangle oldBounds;
	/** Element to manipulate. */
	private final Region element;
		
	/**
	 * Create a command that can resize and/or move a shape. 
	 * @param shape	the shape to manipulate
	 * @param req		the move and resize request
	 * @param newBounds the new size and location
	 * @throws IllegalArgumentException if any of the parameters is null
	 */
	public RegionConstraintChange(Region element, Rectangle newBounds) {
		if (element == null || newBounds == null) {
			throw new IllegalArgumentException();
		}
		setLabel("move/resize");
		this.element = element;
		this.newBounds = newBounds.getCopy();
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
