package com.kesdip.designer.command;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ModelElement;

public class ModelElementMove extends Command {
	private final EditPart element;
	private final boolean isMoveUp;
	private boolean wasMoved;
	
	public ModelElementMove(EditPart element, boolean isMoveUp) {
		this.element = element;
		this.isMoveUp = isMoveUp;
		this.wasMoved = false;
		setLabel("element move " + (isMoveUp ? "up" : "down"));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return wasMoved;
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
		ModelElement child = (ModelElement) element.getModel();
		
		// move the element
		if (isMoveUp) {
			child.getParent().moveChildUp(child);
		} else {
			child.getParent().moveChildDown(child);
		}
		
		wasMoved = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		ModelElement child = (ModelElement) element.getModel();

		// move the element back
		if (isMoveUp) {
			child.getParent().moveChildDown(child);
		} else {
			child.getParent().moveChildUp(child);
		}
	}

}
