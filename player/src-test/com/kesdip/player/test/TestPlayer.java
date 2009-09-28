package com.kesdip.player.test;

import org.quartz.SchedulerException;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;

public class TestPlayer extends Player {

	private final String DEPLOYMENT_XML = "C:/Stelios/Development/Digital Signage/Scenarios/Full-screen TV/fs-tv.des.xml";
	
	public TestPlayer() throws SchedulerException {
		this.monitor = new TimingMonitor(this, true);
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
