package com.kesdip.player.preview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.InputStreamResource;

import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;
import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.Resource;
import com.kesdip.player.components.RootContainer;
import com.kesdip.player.constenum.SystemPropertiesKeys;

/**
 * Utility class which can launch a stand-alone player.
 * 
 * @author gerogias
 */
public class PlayerPreview extends Player {

	private static final Logger logger = Logger.getLogger(PlayerPreview.class);

	private boolean standaloneProcess = false;

	public PlayerPreview() throws SchedulerException {
		this.monitor = new TimingMonitor(this, true);
	}

	public void initialize() {
		new Thread(this.monitor, "monitor").start();
		this.completeDeployment = true;
		this.stopRunning = new AtomicBoolean(false);
	}

	public static void previewPlayer(String path, String vlcPath,
			String mPlayerFile) throws Exception {
		previewPlayer(path, vlcPath, mPlayerFile, false);
	}

	public static void previewPlayer(String path, String vlcPath,
			String mPlayerFile, boolean standalone) throws Exception {
		System.setProperty(SystemPropertiesKeys.KESDIP_EPE_DESIGNER_VLC_PATH,
				vlcPath);
		System.setProperty(
				SystemPropertiesKeys.KESDIP_EPE_DESIGNER_MPLAYER_FILE,
				mPlayerFile);

		PlayerPreview preview = new PlayerPreview();
		preview.initialize();
		preview.standaloneProcess = standalone;

		preview.monitor.startDeployment(-1, path);

		new Thread(preview).start();
	}

	@Override
	protected void playerExited() {
		if (standaloneProcess) {
			logger.info("Preview process stopped.");
			System.exit(0);
		}
	}

	public static Set<String> getResourcePaths(String path)
			throws FileNotFoundException {

		return getResourcePaths(new FileInputStream(new File(path)));
	}

	public static Set<String> getResourcePaths(InputStream xmlStream) {
		Set<String> retVal = new HashSet<String>();
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(beanFactory);
		beanReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
		beanReader.loadBeanDefinitions(new InputStreamResource(xmlStream));
		DeploymentContents deploymentContents = (DeploymentContents) beanFactory
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

	public static String canPreview(String path) throws FileNotFoundException {

		return canPreview(new FileInputStream(new File(path)));
	}

	public static String canPreview(InputStream xmlStream) {
		try {
			DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
			XmlBeanDefinitionReader beanReader = new XmlBeanDefinitionReader(beanFactory);
			beanReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
			beanReader.loadBeanDefinitions(new InputStreamResource(xmlStream));
			DeploymentContents deploymentContents = (DeploymentContents) beanFactory
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
						} else {
							String fileCrc = String.valueOf(FileUtils.getCrc(f)
									.getValue());
							String resCrc = StringUtils.extractCrc(r
									.getChecksum());
							if (!fileCrc.equals(resCrc)) {
								corruptResources.append("\t"
										+ r.getIdentifier() + "\n");
								corruptResourcesExist = true;
							}
						}
					}
				}
			}

			String retVal = "";
			if (missingResourcesExist) {
				retVal += missingResources.toString();
			}
			if (corruptResourcesExist) {
				retVal += corruptResources.toString();
			}

			return retVal.length() == 0 ? null : retVal;
		} catch (BeansException be) {
			return "Error while trying to load the player components: "
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
		if (args == null || args.length < 3) {
			showUsage();
			return;
		}
		if (StringUtils.isEmpty(args[0]) || StringUtils.isEmpty(args[1])) {
			showUsage();
			return;
		}
		try {
			previewPlayer(args[0], args[1], args[2], true);
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
		usage.append("java ").append(PlayerPreview.class.getName()).append(
				" [XML] [VLC_HOME] [MPLAYER_FILE]\n");
		usage.append("\twhere\n");
		usage.append("\tXML is the location of the content XML file\n");
		usage.append("\tVLC_HOME is the location of the VLC installation\n");
		usage
				.append("\tMPLAYER_FILE is the location of the MPlayer executable");
		System.err.println(usage.toString());
	}

}
