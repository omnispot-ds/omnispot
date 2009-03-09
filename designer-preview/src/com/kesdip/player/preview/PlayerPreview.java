package com.kesdip.player.preview;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.common.util.FileUtils;
import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;
import com.kesdip.player.components.Resource;
import com.kesdip.player.components.RootContainer;

public class PlayerPreview extends Player {
	
	public PlayerPreview() throws SchedulerException {
		super();
	}

	public void initialize() {
		this.completeDeployment = false;
		this.completeLayout = false;
		this.stopRunning = new AtomicBoolean(false);
	}

	public static void previewPlayer(String path, String vlcPath) throws Exception {
		System.setProperty("KESDIP_EPE_DESIGNER_VLC_PATH", vlcPath);
		
		PlayerPreview preview = new PlayerPreview();
		new Thread(preview).start();
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext(path);
		DeploymentSettings deploymentSettings = (DeploymentSettings)
				ctx.getBean("deploymentSettings");
		DeploymentContents deploymentContents = (DeploymentContents)
				ctx.getBean("deploymentContents");
		preview.startDeployment(ctx, deploymentSettings, deploymentContents);
	}
	
	public static Set<String> getResourcePaths(String path) {
		Set<String> retVal = new HashSet<String>();
		ApplicationContext ctx = new FileSystemXmlApplicationContext(path);
		DeploymentContents deploymentContents = (DeploymentContents)
				ctx.getBean("deploymentContents");
		List<DeploymentLayout> layouts = deploymentContents.getLayouts();
		for (DeploymentLayout layout : layouts) {
			List<RootContainer> roots = layout.getContentRoots();
			for (RootContainer root : roots) {
				Set<Resource> resources = root.gatherResources();
				for (Resource r : resources) {
					retVal.add(r.getIdentifier());
				}
			}
		}
		
		return retVal;
	}
	
	public static String canPreview(String path) {
		try {
			ApplicationContext ctx = new FileSystemXmlApplicationContext(path);
			DeploymentContents deploymentContents = (DeploymentContents)
					ctx.getBean("deploymentContents");
			StringBuilder missingResources = new StringBuilder("There are missing resources. " +
					"The following resources could not be found:\n");
			boolean missingResourcesExist = false;
			StringBuilder corruptResources = new StringBuilder("There are corrupt resources. " +
					"The following resources can be found but have corrupt contents:\n");
			boolean corruptResourcesExist = false;
			List<DeploymentLayout> layouts = deploymentContents.getLayouts();
			for (DeploymentLayout layout : layouts) {
				List<RootContainer> roots = layout.getContentRoots();
				for (RootContainer root : roots) {
					Set<Resource> resources = root.gatherResources();
					for (Resource r : resources) {
						File f = new File(r.getIdentifier());
						if (!f.exists()) {
							missingResources.append("\t" + r.getIdentifier() + "\n");
							missingResourcesExist = true;
						} else if (!String.valueOf(FileUtils.getCrc(f).getValue()).
								equals(r.getChecksum())) {
							corruptResources.append("\t" + r.getIdentifier() + "\n");
							corruptResourcesExist = true;
						}
					}
				}
			}
			
			String retVal = "";
			if (missingResourcesExist)
				retVal += missingResources.toString();
			if (corruptResourcesExist)
				retVal += corruptResources.toString();
			
			return retVal.length() == 0 ? null : retVal;
		} catch (BeansException be) {
			return "Error while trying to load the load the player components: " +
				be.getRootCause().getMessage();
		}
	}
	
}
