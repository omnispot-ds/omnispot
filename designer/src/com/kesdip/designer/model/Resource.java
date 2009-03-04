package com.kesdip.designer.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.utils.DOMHelpers;
import com.kesdip.designer.utils.FileUtils;

public class Resource {
	private String resource;
	private String cronExpression;
	
	public Resource(String resource, String cronExpression) {
		this.resource = resource;
		this.cronExpression = cronExpression;
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
		return new Resource(other.resource, other.cronExpression);
	}
	
	protected Element serialize(Document doc) {
		Element resourceElement = doc.createElement("bean");
		resourceElement.setAttribute("class", "com.kesdip.player.components.Resource");
		DOMHelpers.addProperty(doc, resourceElement, "identifier", resource);
		if (cronExpression != null && cronExpression.length() != 0)
			DOMHelpers.addProperty(doc, resourceElement, "cronExpression", cronExpression);
		DOMHelpers.addProperty(doc, resourceElement, "checksum",
				String.valueOf(FileUtils.getChecksum(resource)));
		return resourceElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		resource = DOMHelpers.getSimpleProperty(componentNode, "identifier");
		String cronString = DOMHelpers.getSimpleProperty(componentNode, "cronExpression");
		if (cronString != null)
			cronExpression = cronString;
	}
	
	public void checkEquivalence(Resource other) {
		assert(resource.equals(other.resource));
		assert(cronExpression.equals(other.cronExpression));
	}
	
	@Override
	public String toString() {
		return "Resource (" + resource + "," + cronExpression + ")";
	}
}