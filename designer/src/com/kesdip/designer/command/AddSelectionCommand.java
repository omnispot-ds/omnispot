package com.kesdip.designer.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.parts.RegionEditPart;

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
			if (editPart.getParent() instanceof RegionEditPart) {
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
			if (parent instanceof Deployment) {
				// TODO Implement properly
				Deployment deployment = (Deployment) parent;
				Layout layout = (Layout) child;
				deployment.addLayout(layout);
			} else if (parent instanceof Layout) {
				// TODO Implement properly
				Layout layout = (Layout) parent;
				Region region = (Region) child;
				layout.addRegion(region);
			} else if (parent instanceof Region) {
				Region region = (Region) parent;
				ComponentModelElement elem = (ComponentModelElement) child;
				elem.setLocation(elem.getLocation().translate(region.getLocation()));
				region.addComponent(elem);
			} else
				throw new RuntimeException("Unexpected parent: " +
						parent.getClass().getName());
		}
		wasAdded = true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for (Object o : elements)  {
			ModelElement child = (ModelElement) o;
			ModelElement parent = child.getDeployment().removeChild(child);
			if (parent instanceof Deployment) {
				// TODO Implement properly
			} else if (parent instanceof Layout) {
				// TODO Implement properly
			} else if (parent instanceof Region) {
				Region region = (Region) parent;
				ComponentModelElement elem = (ComponentModelElement) child;
				elem.setLocation(elem.getLocation().translate(region.getLocation().getNegated()));
			}
		}
	}
}
