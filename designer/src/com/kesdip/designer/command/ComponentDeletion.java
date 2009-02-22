package com.kesdip.designer.command;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Region;

public class ComponentDeletion extends Command {
	/** Element to delete */
	private final ComponentModelElement element;
	/** Layout to delete from */
	private final Layout layout;
	/** Region to delete from */
	private final Region region;
	/** True, if child was removed from its parent. */
	private boolean wasRemoved;
	
	public ComponentDeletion(Layout layout, Region region) {
		if (layout == null || region == null) {
			throw new IllegalArgumentException();
		}
		setLabel("region deletion");
		this.element = null;
		this.region = region;
		this.layout = layout;
		this.wasRemoved = false;
	}
	
	public ComponentDeletion(Region region, ComponentModelElement element) {
		if (region == null || element == null) {
			throw new IllegalArgumentException();
		}
		setLabel("component deletion");
		this.element = element;
		this.region = region;
		this.layout = null;
		this.wasRemoved = false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return wasRemoved;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		redo();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		// remove the element
		if (layout == null)
			wasRemoved = region.removeComponent(element);
		else
			wasRemoved = layout.removeRegion(region);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		// add the element
		if (layout == null)
			region.addComponent(element);
		else
			layout.addRegion(region);
	}

}
