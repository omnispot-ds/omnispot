package com.kesdip.player;

import java.util.List;

import com.kesdip.player.components.RootContainer;

/**
 * Holder of the deployment contents (all the content roots are contained in
 * an instance of this class). This helps provide some structure to the
 * deployment descriptor.
 * 
 * @author Pafsanias Ftakas
 */
public class DeploymentContents {
	private List<RootContainer> contentRoots;
	
	public void setContentRoots(List<RootContainer> contentRoots) {
		this.contentRoots = contentRoots;
	}
	
	List<RootContainer> getContentRoots() {
		return contentRoots;
	}
}
