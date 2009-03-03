package com.kesdip.designer.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;

/**
 * The way the eclipse framework is written, this component deletion command is part of
 * a compound command. If the user happens to select delete when say 3 editparts are
 * selected, then the result would be one compound command with three component deletion
 * command underneath with the same list of objects in all of them marked for deletion.
 * 
 * This is the reason that that we are generous when deleting if we do not find the
 * elements there.
 */
public class ComponentDeletion extends Command {
	/** parent elements */
	private Map<ModelElement, ModelElement> parents;
	/** Elements to delete */
	@SuppressWarnings("unchecked")
	private final List elements;
	/** True, if child was removed from its parent. */
	private boolean wasRemoved;
	
	@SuppressWarnings("unchecked")
	public ComponentDeletion(List elements) {
		if (elements == null || elements.size() == 0) {
			throw new IllegalArgumentException();
		}
		setLabel("element(s) deletion");
		this.elements = new ArrayList();
		for (Object o : elements) {
			EditPart editPart = (EditPart) o;
			ModelElement elem = (ModelElement) editPart.getModel();
			this.elements.add(elem);
		}
		this.wasRemoved = false;
		this.parents = new HashMap<ModelElement, ModelElement>();
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
		// remove the elements
		parents.clear();
		boolean allOK = true;
		for (Object o : elements)  {
			ModelElement child = (ModelElement) o;
			ModelElement parent = child.getParent();
			if (parent != null) {
				parents.put(child, parent);
				parent.removeChild(child);
			} else {
				allOK = false;
			}
		}
		wasRemoved = allOK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		// add the elements
		for (Object o : elements) {
			ModelElement child = (ModelElement) o;
			ModelElement parent = parents.get(child);
			if (parent == null)
				continue;
			
			if (parent instanceof Deployment)
				((Deployment) parent).add((Layout) child);
			else if (parent instanceof Layout)
				((Layout) parent).add((Region) child);
			else if (parent instanceof Region)
				((Region) parent).add((ComponentModelElement) child);
			else
				throw new RuntimeException("Unexpected parent: " +
						parent.getClass().getName());
		}
	}

}
