package com.kesdip.designer.editor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.kesdip.designer.command.AddSelectionCommand;
import com.kesdip.designer.command.ComponentDeletion;
import com.kesdip.designer.command.ModelElementMove;
import com.kesdip.designer.command.RemoveSelectionCommand;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.Deployment;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.ModelElement;
import com.kesdip.designer.model.Region;

public class DesignerComponentEditPolicy extends ComponentEditPolicy {
	public static final String REQ_MOVE_UP = "move element up";
	public static final String REQ_MOVE_DOWN = "move element down";
	public static final String REQ_CUT = "cut to clipboard";
	public static final String REQ_PASTE = "paste from clipboard";

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		if (deleteRequest.getEditParts().size() != 0)
			return new ComponentDeletion(deleteRequest.getEditParts());
		return super.createDeleteCommand(deleteRequest);
	}

	protected Command getCutCommand(GroupRequest cutRequest) {
		return new RemoveSelectionCommand(cutRequest.getEditParts());
	}
	
	@SuppressWarnings("unchecked")
	protected Command getPasteCommand(GroupRequest pasteRequest) {
		List selections = (List) pasteRequest.getExtendedData().get("context");
		if (selections == null || selections.size() != 1)
			return null;
		if (!(selections.get(0) instanceof EditPart))
			return null;
		Object target = ((EditPart) selections.get(0)).getModel();
		Set<String> typeSet = new HashSet<String>();
		for (Object o : pasteRequest.getEditParts()) {
			EditPart part = (EditPart) o;
			ModelElement element = (ModelElement) part.getModel();
			if (element instanceof Deployment)
				typeSet.add("Deployment");
			else if (element instanceof Layout)
				typeSet.add("Layout");
			else if (element instanceof Region)
				typeSet.add("Region");
			else if (element instanceof ComponentModelElement)
				typeSet.add("ComponentModelElement");
			else
				throw new RuntimeException("Unexpected cut object: " +
						element.getClass().getName());
		}
		if (typeSet.size() == 1 && !typeSet.iterator().next().equals("Deployment")) {
			String childrenType = typeSet.iterator().next();
			if (target instanceof Deployment && childrenType.equals("Layout") ||
					target instanceof Layout && childrenType.equals("Region") ||
					target instanceof Region && childrenType.equals("ComponentModelElement"))
				return new AddSelectionCommand(
						(ModelElement) target, pasteRequest.getEditParts());
		}
		return null;
	}
	
	protected Command createMoveUpCommand(GroupRequest request) {
		EditPart editPart = (EditPart) request.getEditParts().get(0);
		if (!(editPart.getModel() instanceof ModelElement))
			return null;
		ModelElement element = (ModelElement) editPart.getModel();
		if (element.getParent() == null  || element.getParent().isFirstChild(element))
			return null;

		return new ModelElementMove(editPart, true);
	}
	
	protected Command createMoveDownCommand(GroupRequest request) {
		EditPart editPart = (EditPart) request.getEditParts().get(0);
		if (!(editPart.getModel() instanceof ModelElement))
			return null;
		ModelElement element = (ModelElement) editPart.getModel();
		if (element.getParent() == null  || element.getParent().isLastChild(element))
			return null;
		
		return new ModelElementMove(editPart, false);
	}

	public Command getCommand(Request request) {
		if (REQ_CUT.equals(request.getType())) {
			return getCutCommand((GroupRequest)request);
		} else if (REQ_PASTE.equals(request.getType())) {
			return getPasteCommand((GroupRequest)request);
		} else if (REQ_MOVE_UP.equals(request.getType())) {
			return createMoveUpCommand((GroupRequest) request);
		} else if (REQ_MOVE_DOWN.equals(request.getType())) {
			return createMoveDownCommand((GroupRequest) request);
		}
		return super.getCommand(request);
	}

}
