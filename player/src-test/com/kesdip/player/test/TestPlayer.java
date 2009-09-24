package com.kesdip.player.test;

import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;

public class TestPlayer extends Player {
	
	public TestPlayer() throws SchedulerException {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("C:\\Documents and Settings\\gerogias\\Desktop\\ticker.des.xml");
		DeploymentSettings deploymentSettings = (DeploymentSettings) ctx.getBean("deploymentSettings");
		DeploymentContents deploymentContents = (DeploymentContents) ctx.getBean("deploymentContents");
		try {
			startDeployment(ctx, deploymentSettings, deploymentContents);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
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
			new Thread(player, "testPlayer").start();
			player.initialize();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
