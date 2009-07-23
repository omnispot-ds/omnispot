package com.kesdip.designer.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.LayoutGuide;
import com.kesdip.designer.model.LayoutRuler;

public class DeleteGuideCommand extends Command {

	private LayoutRuler parent;
	private LayoutGuide guide;
	private Map<ComponentModelElement, Integer> oldParts;

	public DeleteGuideCommand(LayoutGuide guide, LayoutRuler parent) {
		super("Delete Guide");
		this.guide = guide;
		this.parent = parent;
	}

	public boolean canUndo() {
		return true;
	}

	public void execute() {
		oldParts = new HashMap<ComponentModelElement, Integer>(guide.getMap());
		Iterator<ComponentModelElement> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			guide.detachPart(iter.next());
		}
		parent.removeGuide(guide);
	}
	public void undo() {
		parent.addGuide(guide);
		Iterator<ComponentModelElement> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			ComponentModelElement part = (ComponentModelElement)iter.next();
			guide.attachPart(part, ((Integer)oldParts.get(part)).intValue());
		}
	}
}
