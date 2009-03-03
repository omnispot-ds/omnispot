package com.kesdip.designer.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;

public class AddSelectionCommand extends Command {
	/** parent element */
	private ModelElement parent;
	/** Elements to delete */
	@SuppressWarnings({ "unchecked" })
	private final List elements;
	/** True, if child was removed from its parent. */
	private boolean wasAdded;
	
	@SuppressWarnings("unchecked")
	public AddSelectionCommand(ModelElement parent, List elements) {
		setLabel("element addition");
		this.parent = parent;
		this.elements = new ArrayList();
		for (Object o : elements) {
			EditPart editPart = (EditPart) o;
			ModelElement elem = ((ModelElement) editPart.getModel()).deepCopy();
			if (editPart.getParent().getModel() instanceof Region) {
				Region p = (Region) editPart.getParent().getModel();
				ComponentModelElement c = (ComponentModelElement) elem;
				c.setLocation(c.getLocation().translate(p.getLocation().getNegated()));
			}
			this.elements.add(elem);
		}
		wasAdded = false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return wasAdded;
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
		for (Object o : elements) {
			ModelElement child = (ModelElement) o;
			if (parent instanceof Region) {
				Region region = (Region) parent;
				ComponentModelElement elem = (ComponentModelElement) child;
				elem.setLocation(elem.getLocation().translate(region.getLocation()));
			}
			parent.add(child);
		}
		wasAdded = true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for (Object o : elements)  {
			ModelElement child = (ModelElement) o;
			ModelElement parent = child.getParent();
			if (parent != null) {
				if (parent instanceof Region) {
					Region p = (Region) parent;
					ComponentModelElement c = (ComponentModelElement) child;
					c.setLocation(c.getLocation().translate(p.getLocation().getNegated()));
				}
				parent.removeChild(child);
			}
		}
	}
}
