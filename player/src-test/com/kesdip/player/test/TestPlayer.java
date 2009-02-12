package com.kesdip.player.test;

import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;

public class TestPlayer extends Player {
	
	public TestPlayer() throws SchedulerException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("appContext.xml", TestPlayer.class);
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
		// TODO Auto-generated method stub
		//super.initialize();
	}
	
	public static void main(String[] args) {
		try {
			TestPlayer player = new TestPlayer();
			new Thread(player, "testPlayer").start();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
