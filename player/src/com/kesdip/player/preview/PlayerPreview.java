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
import com.kesdip.common.util.StringUtils;
import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;
import com.kesdip.player.components.Resource;
import com.kesdip.player.components.RootContainer;

/**
 * Utility class which can launch a stand-alone player.
 * 
 * @author gerogias
 */
public class PlayerPreview extends Player {
	
	private boolean standaloneProcess = false;
	
	public PlayerPreview() throws SchedulerException {
		super();
	}

	public void initialize() {
		this.completeDeployment = false;
		this.completeLayout = false;
		this.stopRunning = new AtomicBoolean(false);
	}
	
	public static void previewPlayer(String path, String vlcPath) throws Exception {
		previewPlayer(path, vlcPath, false);
	}

	public static void previewPlayer(String path, String vlcPath, boolean standalone)
			throws Exception {
		System.out.println("launching PlayerPreview with " + path + " and " + vlcPath);
		System.setProperty("KESDIP_EPE_DESIGNER_VLC_PATH", vlcPath);

		PlayerPreview preview = new PlayerPreview();
		preview.standaloneProcess = standalone;
		
		new Thread(preview).start();

		ApplicationContext ctx = new FileSystemXmlApplicationContext(path);
		DeploymentSettings deploymentSettings = (DeploymentSettings) ctx
				.getBean("deploymentSettings");
		DeploymentContents deploymentContents = (DeploymentContents) ctx
				.getBean("deploymentContents");
		preview.startDeployment(ctx, deploymentSettings, deploymentContents);
	}
	
	@Override
	protected void playerExited() {
		if (standaloneProcess) {
			System.out.println("Preview process stopped.");
			System.exit(0);
		}
	}

	public static Set<String> getResourcePaths(String path) {
		Set<String> retVal = new HashSet<String>();
		ApplicationContext ctx = new FileSystemXmlApplicationContext(path);
		DeploymentContents deploymentContents = (DeploymentContents) ctx
				.getBean("deploymentContents");
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
			DeploymentContents deploymentContents = (DeploymentContents) ctx
					.getBean("deploymentContents");
			StringBuilder missingResources = new StringBuilder(
					"There are missing resources. "
							+ "The following resources could not be found:\n");
			boolean missingResourcesExist = false;
			StringBuilder corruptResources = new StringBuilder(
					"There are corrupt resources. "
							+ "The following resources can be found but have corrupt contents:\n");
			boolean corruptResourcesExist = false;
			List<DeploymentLayout> layouts = deploymentContents.getLayouts();
			for (DeploymentLayout layout : layouts) {
				List<RootContainer> roots = layout.getContentRoots();
				for (RootContainer root : roots) {
					Set<Resource> resources = root.gatherResources();
					for (Resource r : resources) {
						File f = new File(r.getIdentifier());
						if (!f.exists()) {
							missingResources.append("\t" + r.getIdentifier()
									+ "\n");
							missingResourcesExist = true;
						} else if (!String.valueOf(
								FileUtils.getCrc(f).getValue()).equals(
								r.getChecksum())) {
							corruptResources.append("\t" + r.getIdentifier()
									+ "\n");
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
			return "Error while trying to load the load the player components: "
					+ be.getRootCause().getMessage();
		}
	}

	/**
	 * Main method. Accepts exactly 2 arguments in the following order: the path
	 * to the deployment XML and the path to VLC_HOME.
	 * 
	 * @param args
	 *            the arguments.
	 */
	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			showUsage();
			return;
		}
		if (StringUtils.isEmpty(args[0]) || StringUtils.isEmpty(args[1])) {
			showUsage();
			return;
		}
		try {
			previewPlayer(args[0], args[1], true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Prints usage information to the console.
	 */
	private static final void showUsage() {
		StringBuilder usage = new StringBuilder("Usage:\n");
		usage.append("java ").append(PlayerPreview.class.getName()).append(" [XML] [VLC_HOME]\n");
		usage.append("\twhere\n");
		usage.append("\tXML is the location of the content XML file\n");
		usage.append("\tVLC_HOME is the location of the VLC installation");
		System.err.println(usage.toString());
	}

}
