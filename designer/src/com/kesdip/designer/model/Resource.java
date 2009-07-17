package com.kesdip.designer.model;

import java.io.File;

import org.eclipse.ui.IMemento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.common.util.FileUtils;
import com.kesdip.designer.utils.DOMHelpers;

public class Resource {
	private String resource;
	private String cronExpression;
	/**
	 * Indicates a full-screen video.
	 * 
	 * FIXME Move this property to an appropriate subclass.
	 */
	private boolean fullscreen;

	public Resource(String resource, String cronExpression) {
		this.resource = resource;
		this.cronExpression = cronExpression;
	}

	public Resource(String resource, String cronExpression, boolean fullscreen) {
		this.resource = resource;
		this.cronExpression = cronExpression;
		this.fullscreen = fullscreen;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public static Resource deepCopy(Resource other) {
		return new Resource(other.resource, other.cronExpression,
				other.fullscreen);
	}

	protected Element serialize(Document doc, boolean isPublish) {
		Element resourceElement = doc.createElement("bean");
		resourceElement.setAttribute("class",
				"com.kesdip.player.components.Resource");
		File file = new File(resource);
		if (isPublish) {
			DOMHelpers.addProperty(doc, resourceElement, "identifier", file
					.getName());
		} else {
			DOMHelpers
					.addProperty(doc, resourceElement, "identifier", resource);
		}
		if (cronExpression != null && cronExpression.length() != 0) {
			DOMHelpers.addProperty(doc, resourceElement, "cronExpression",
					cronExpression);
		}
		DOMHelpers.addProperty(doc, resourceElement, "checksum", String
				.valueOf(FileUtils.getCrc(file).getValue())
				+ '-' + String.valueOf(FileUtils.getSize(file)));
		if (fullscreen) {
			DOMHelpers.addMapValue(resourceElement, "attributes", "fullScreen",
					Boolean.toString(fullscreen));
		}

		return resourceElement;
	}

	protected void deserialize(Document doc, Node componentNode) {
		resource = DOMHelpers.getSimpleProperty(componentNode, "identifier");
		String cronString = DOMHelpers.getSimpleProperty(componentNode,
				"cronExpression");
		if (cronString != null) {
			cronExpression = cronString;
		}
		String fullScreenStr = DOMHelpers.getMapValue(componentNode,
				"attributes", "fullScreen");
		fullscreen = "true".equalsIgnoreCase(fullScreenStr);
	}

	public void save(IMemento memento) {
		memento.putString(ModelElement.TAG_RESOURCE, resource);
		memento.putString(ModelElement.TAG_CRON_EXPRESSION, cronExpression);
		memento.putBoolean(ModelElement.TAG_FULL_SCREEN, fullscreen);
	}

	public void load(IMemento memento) {
		resource = memento.getString(ModelElement.TAG_RESOURCE);
		cronExpression = memento.getString(ModelElement.TAG_CRON_EXPRESSION);
		fullscreen = memento.getBoolean(ModelElement.TAG_FULL_SCREEN);
	}

	public void checkEquivalence(Resource other) {
		assert (resource.equals(other.resource));
		assert (cronExpression.equals(other.cronExpression));
		assert (fullscreen == other.fullscreen);
	}

	@Override
	public String toString() {
		return "Resource (" + resource + "," + cronExpression + ","
				+ fullscreen + ")";
	}

	/**
	 * @return the fullscreen.
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * @param fullscreen
	 *            the fullscreen to set
	 */
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}
}
