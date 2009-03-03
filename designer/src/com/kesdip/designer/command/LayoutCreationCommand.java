package com.kesdip.designer.command;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class LayoutCreationCommand extends Command {
	private final Layout layout;
	private final Deployment deployment;
	
	public LayoutCreationCommand(Layout layout, Deployment deployment) {
		this.layout = layout;
		this.deployment = deployment;
		setLabel("layout creation");
	}
	
	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return layout != null && deployment != null;
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
		deployment.add(layout);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		deployment.removeChild(layout);
	}
}
