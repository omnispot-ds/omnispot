package com.kesdip.designer.command;

import java.util.List;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;

public class LayoutDeletionCommand extends Command {
	@SuppressWarnings("unchecked")
	private final List layoutList;
	private final Deployment deployment;
	
	@SuppressWarnings("unchecked")
	public LayoutDeletionCommand(List layoutList, Deployment deployment) {
		this.layoutList = layoutList;
		this.deployment = deployment;
		setLabel("layout creation");
	}
	
	/**
	 * Can execute if all the necessary information has been provided. 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		return layoutList != null && deployment != null;
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
		for (Object o : layoutList) {
			if (!(o instanceof Layout))
				continue;
			Layout layout = (Layout) o;
			deployment.removeChild(layout);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		for (Object o : layoutList) {
			if (!(o instanceof Layout))
				continue;
			Layout layout = (Layout) o;
			deployment.removeChild(layout);
		}
	}
}
