package com.kesdip.designer.command;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.LayoutGuide;
import com.kesdip.designer.model.LayoutRuler;

public class CreateGuideCommand extends Command {

	private LayoutGuide guide;
	private LayoutRuler parent;
	private int position;

	public CreateGuideCommand(LayoutRuler parent, int position) {
		super("Create Guide");
		this.parent = parent;
		this.position = position;
	}

	public boolean canUndo() {
		return true;
	}

	public void execute() {
		if (guide == null)
			guide = new LayoutGuide(!parent.isHorizontal());
		guide.setPosition(position);
		parent.addGuide(guide);
	}

	public void undo() {
		parent.removeGuide(guide);
	}

}
