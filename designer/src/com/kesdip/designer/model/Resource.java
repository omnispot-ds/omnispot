package com.kesdip.designer.model;

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
	
	@Override
	public String toString() {
		return "Resource (" + resource + "," + cronExpression + ")";
	}
}
