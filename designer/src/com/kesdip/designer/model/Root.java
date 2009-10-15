package com.kesdip.designer.model;

public class Root {
	private Deployment deployment;
	
	public Root(Deployment deployment) {
		this.deployment = deployment;
	}
	
	public Deployment getDeployment() {
		return deployment;
	}
}
