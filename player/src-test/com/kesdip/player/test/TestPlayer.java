package com.kesdip.player.test;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;

public class TestPlayer extends Player {

	private final String DEPLOYMENT_XML = "C:/Stelios/Development/Digital Signage/Scenarios/Metro 1/metro1.des.xml";
	
	public TestPlayer() throws SchedulerException {
		// do nothing
	}
	
	@Override
	public void initialize() {
		try {
			super.initialize();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.completeDeployment = true;
		try {
			super.monitor.startDeployment(-1, DEPLOYMENT_XML);
		} catch (Exception se) {
			throw new RuntimeException(se);
		}
	}
	
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestPlayer player = new TestPlayer();
			player.initialize();
			new Thread(player, "testPlayer").start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
