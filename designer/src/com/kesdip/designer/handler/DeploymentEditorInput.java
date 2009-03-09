package com.kesdip.designer.handler;

import java.io.File;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.kesdip.designer.model.Deployment;

public class DeploymentEditorInput extends PlatformObject implements IEditorInput {
	private Deployment deployment;
	private String path;
	
	public DeploymentEditorInput(Deployment deployment, String path) {
		this.deployment = deployment;
		this.path = path;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ImageDescriptor.getMissingImageDescriptor();
	}

	@Override
	public String getName() {
		if (path != null) {
			File f = new File(path);
			return f.getName();
		}
		return deployment.toString();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return deployment.toString();
	}
	
	public Deployment getDeployment() {
		return deployment;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DeploymentEditorInput))
			return false;
		
		DeploymentEditorInput dei = (DeploymentEditorInput) other;
		return deployment.equals(dei.deployment);
	}

}
