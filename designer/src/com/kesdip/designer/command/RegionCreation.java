package com.kesdip.designer.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Region;

public class RegionCreation extends Command {

	/** The new element. */ 
	private Region element;
	/** The deployment diagram to add to. */
	private final Layout parent;
	/** The bounds of the new element. */
	private Rectangle bounds;

	/**
	 * Create a command that will add a new Region to a Layout.
	 * @param element the new Region that is to be added
	 * @param parent the Layout that will hold the new element
	 * @param bounds the bounds of the new elemeny; the size can be (-1, -1) if not known
	 * @throws IllegalArgumentException if any parameter is null, or the request
	 * 						  does not provide a new Shape instance
	 */
	public RegionCreation(Region element, Layout parent, Rectangle bounds) {
		this.element = element;
		this.parent = parent;
		this.bounds = bounds;
		setLabel("region creation");
	}

	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return element != null && parent != null && bounds != null;
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
