package com.kesdip.designer.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Region;

public class ComponentCreation extends Command {

	/** The new element. */ 
	private ComponentModelElement element;
	/** The deployment diagram to add to. */
	private final Region parent;
	/** The bounds of the new element. */
	private Rectangle bounds;

	/**
	 * Create a command that will add a new Shape to a ShapesDiagram.
	 * @param element the new AdElement that is to be added
	 * @param parent the AdDeployment that will hold the new element
	 * @param bounds the bounds of the new elemeny; the size can be (-1, -1) if not known
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new Shape instance
	 */
	public ComponentCreation(ComponentModelElement element, Region parent, Rectangle bounds) {
		this.element = element;
		this.parent = parent;
		this.bounds = bounds;
		setLabel("element creation");
	}

	/**
	 * Can execute if all the necessary information has been provided.
	 * Cannot execute if both are of the same class and the element 
	 * already has a parent. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return element != null && parent != null && bounds != null 
			&& !element.getClass().equals(parent.getClass())
			&& element.getParent() == null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		element.setLocation(bounds.getLocation());
		Dimension size = bounds.getSize();
		if (size.width > 0 && size.height > 0)
			element.setSize(size);
		redo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		parent.add(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.removeChild(element);
	}
}
