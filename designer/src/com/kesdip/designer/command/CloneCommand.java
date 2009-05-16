package com.kesdip.designer.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.kesdip.designer.model.ClockComponent;
import com.kesdip.designer.model.ComponentModelElement;
import com.kesdip.designer.model.FlashComponent;
import com.kesdip.designer.model.FlashWeatherComponent;
import com.kesdip.designer.model.ImageComponent;
import com.kesdip.designer.model.Layout;
import com.kesdip.designer.model.LayoutGuide;
import com.kesdip.designer.model.Region;
import com.kesdip.designer.model.TickerComponent;
import com.kesdip.designer.model.TunerVideoComponent;
import com.kesdip.designer.model.VideoComponent;

public class CloneCommand extends Command {

	private List<ComponentModelElement> parts, newTopLevelParts;
	private Layout parent;
	private Map<ComponentModelElement, Rectangle> bounds;
	private Map<ComponentModelElement, Integer> indices;
	private Map<ComponentModelElement, ComponentModelElement> partsMap;
	private ChangeGuideCommand vGuideCommand, hGuideCommand;
	private LayoutGuide hGuide, vGuide;
	private int hAlignment, vAlignment;

	public CloneCommand() {
		super("Clone Command");
		parts = new LinkedList<ComponentModelElement>();
	}

	public void addPart(ComponentModelElement part, Rectangle newBounds) {
		parts.add(part);
		if (bounds == null) {
			bounds = new HashMap<ComponentModelElement, Rectangle>();
		}
		bounds.put(part, newBounds);
	}

	public void addPart(ComponentModelElement part, int index) {
		parts.add(part);
		if (indices == null) {
			indices = new HashMap<ComponentModelElement, Integer>();
		}
		indices.put(part, new Integer(index));
	}

	protected void clonePart(ComponentModelElement oldPart, Layout newParent,
			Rectangle newBounds,
			Map<ComponentModelElement, ComponentModelElement> partsMap, int index) {
		ComponentModelElement newPart = null;
		
		if (oldPart instanceof Region) {
			newPart = new Region();
		} else if (oldPart instanceof FlashComponent) {
			newPart = new FlashComponent();
		} else if (oldPart instanceof FlashWeatherComponent) {
			newPart = new FlashWeatherComponent();
		} else if (oldPart instanceof ImageComponent) {
			newPart = new ImageComponent();
		} else if (oldPart instanceof ClockComponent) {
			newPart = new ClockComponent();
		} else if (oldPart instanceof TickerComponent) {
			newPart = new TickerComponent();
		} else if (oldPart instanceof TunerVideoComponent) {
			newPart = new TunerVideoComponent();
		} else if (oldPart instanceof VideoComponent) {
			newPart = new VideoComponent();
		}
		
		if (index < 0) {
			newParent.add(newPart);
		} else {
			newParent.insertChildAt(index, newPart);
		}
		
		newPart.setSize(oldPart.getSize());

		
		if (newBounds != null) {
			newPart.setLocation(newBounds.getTopLeft());
		} else {
			newPart.setLocation(oldPart.getLocation());
		}
		
		// keep track of the new parts so we can delete them in undo
		// keep track of the oldpart -> newpart map so that we can properly attach
		// all guides.
		if (newParent == parent)
			newTopLevelParts.add(newPart);
		partsMap.put(oldPart, newPart);
	}

	public void execute() {
		partsMap = new HashMap<ComponentModelElement, ComponentModelElement>();
		newTopLevelParts = new LinkedList<ComponentModelElement>();

		Iterator<ComponentModelElement> i = parts.iterator();
		
		ComponentModelElement part = null;
		while (i.hasNext()) {
			part = i.next();
			if (bounds != null && bounds.containsKey(part)) {
				clonePart(part, parent, (Rectangle)bounds.get(part), partsMap, -1);	
			} else if (indices != null && indices.containsKey(part)) {
				clonePart(part, parent, null, partsMap,
						((Integer)indices.get(part)).intValue());
			} else {
				clonePart(part, parent, null, partsMap, -1);
			}
		}
		
		if (hGuide != null) {
			hGuideCommand = new ChangeGuideCommand(partsMap.get(parts.get(0)), true);
			hGuideCommand.setNewGuide(hGuide, hAlignment);
			hGuideCommand.execute();
		}
			
		if (vGuide != null) {
			vGuideCommand = new ChangeGuideCommand(partsMap.get(parts.get(0)), false);
			vGuideCommand.setNewGuide(vGuide, vAlignment);
			vGuideCommand.execute();
		}
	}

	public void setParent(Layout parent) {
		this.parent = parent;
	}

	public void redo() {
		for (Iterator<ComponentModelElement> iter = newTopLevelParts.iterator(); iter.hasNext();)
			parent.add(iter.next());
		if (hGuideCommand != null)
			hGuideCommand.redo();
		if (vGuideCommand != null)
			vGuideCommand.redo();
	}

	public void setGuide(LayoutGuide guide, int alignment, boolean isHorizontal) {
		if (isHorizontal) {
			hGuide = guide;
			hAlignment = alignment;
		} else {
			vGuide = guide;
			vAlignment = alignment;
		}
	}

	public void undo() {
		if (hGuideCommand != null)
			hGuideCommand.undo();
		if (vGuideCommand != null)
			vGuideCommand.undo();
		for (Iterator<ComponentModelElement> iter = newTopLevelParts.iterator(); iter.hasNext();)
			parent.removeChild(iter.next());
	}

}
