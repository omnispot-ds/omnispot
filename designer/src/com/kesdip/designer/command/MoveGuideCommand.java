package com.kesdip.designer.command;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.LayoutGuide;

public class MoveGuideCommand extends Command {

	private int pDelta;
	private LayoutGuide guide;
		
	public MoveGuideCommand(LayoutGuide guide, int positionDelta) {
		super("Move Guide");
		this.guide = guide;
		pDelta = positionDelta;
	}

	public void execute() {
		guide.setPosition(guide.getPosition() + pDelta);
		Iterator<ComponentModelElement> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			ComponentModelElement part = (ComponentModelElement) iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y += pDelta;
			} else {
				location.x += pDelta;
			}
			part.setLocation(location);
		}
	}

	public void undo() {
		guide.setPosition(guide.getPosition() - pDelta);
		Iterator<ComponentModelElement> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			ComponentModelElement part = (ComponentModelElement)iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y -= pDelta;
			} else {
				location.x -= pDelta;
			}
			part.setLocation(location);
		}
	}

}
