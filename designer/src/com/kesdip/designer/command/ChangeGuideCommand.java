package com.kesdip.designer.command;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.LayoutGuide;

public class ChangeGuideCommand extends Command {
	private ComponentModelElement part;
	private LayoutGuide oldGuide, newGuide;
	private int oldAlign, newAlign;
	private boolean horizontal;

	public ChangeGuideCommand(ComponentModelElement part, boolean horizontalGuide) {
		super();
		this.part = part;
		horizontal = horizontalGuide;
	}

	protected void changeGuide(LayoutGuide oldGuide, LayoutGuide newGuide, int newAlignment) {
		if (oldGuide != null && oldGuide != newGuide) {
			oldGuide.detachPart(part);
		}
		// You need to re-attach the part even if the oldGuide and the newGuide are the same
		// because the alignment could have changed
		if (newGuide != null) {
			newGuide.attachPart(part, newAlignment);
		}
	}

	public void execute() {
		// Cache the old values
		oldGuide = horizontal ? part.getHorizontalGuide() : part.getVerticalGuide();		
		if (oldGuide != null)
			oldAlign = oldGuide.getAlignment(part);
		
		redo();
	}

	public void redo() {
		changeGuide(oldGuide, newGuide, newAlign);
	}

	public void setNewGuide(LayoutGuide guide, int alignment) {
		newGuide = guide;
		newAlign = alignment;
	}

	public void undo() {
		changeGuide(newGuide, oldGuide, oldAlign);
	}

}
