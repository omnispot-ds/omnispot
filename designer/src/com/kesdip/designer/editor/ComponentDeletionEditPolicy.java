package com.kesdip.designer.editor;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.kesdip.designer.command.ComponentDeletion;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.Region;

public class ComponentDeletionEditPolicy extends ComponentEditPolicy {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		if (parent instanceof Layout && child instanceof Region)
			return new ComponentDeletion((Layout) parent, (Region) child);
		else if (parent instanceof Region && child instanceof ComponentModelElement)
			return new ComponentDeletion((Region) parent, (ComponentModelElement) child);
		return super.createDeleteCommand(deleteRequest);
	}
}
