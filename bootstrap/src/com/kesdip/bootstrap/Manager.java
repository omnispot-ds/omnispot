package com.kesdip.bootstrap;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.bootstrap.communication.ProtocolHandler;
import com.kesdip.business.util.schema.SchemaUpdater;

public class Manager extends Thread {

	private static final Logger logger = Logger.getLogger(Manager.class);

	/**
	 * The package containing schema update SQL scripts.
	 */
	public static final String SQL_PKG = "com/kesdip/bootstrap/schema/";
	
	private boolean run = true;

	private long communicationInterval;

	private long screendumpInterval;

	private long intervalsRatio;

	public String serverURL = Config.getSingleton().getServerURL();

	MessagePump pump;

	static ApplicationContext ctx;

	public static void main(String[] args) {
		Manager manager = new Manager();
		manager.start();
	}

	private void init() {
		communicationInterval = Long.parseLong(Config.getSingleton()
				.getCommunicationInterval()) * 1000;
		screendumpInterval = Long.parseLong(Config.getSingleton()
				.getScreenDumpInterval()) * 1000;
		serverURL = Config.getSingleton().getServerURL();
		intervalsRatio = screendumpInterval / communicationInterval;
		
		ctx = new ClassPathXmlApplicationContext("bootstrapContext.xml");

		// update DB before starting message pump
		updateDbSchema();
		
		MessagePump pump = new MessagePump();
		pump.start();
		setPump(pump);
	}

	@Override
	public void run() {
		init();

		int exceptioncount = 0;
		long firstExceptionTimeStamp = 0;
		while (run) {
			try {
				Thread.sleep(communicationInterval);
				ProtocolHandler comm = (ProtocolHandler) ctx
						.getBean("ProtocolHandler");
				comm.setManager(this);
				try {
					comm.performRequest();
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
					exceptioncount++;
					if (firstExceptionTimeStamp == 0)
						firstExceptionTimeStamp = new Date().getTime();
					int comm2excRate; 
					if (exceptioncount == 5) {
						if (new Date().getTime() - firstExceptionTimeStamp < 1800 * 1000) {
							logger
									.error("Exception count equal to 5 in lees than half an hour. Restarting...");
							// windows will restart it hopefully...
							System.exit(0);
						} else {
							exceptioncount = 0;
							firstExceptionTimeStamp = 0;
						}
					}
					logger.info("Continuing operation...");
				}
			} catch (InterruptedException ie) {
				logger
						.error("Manager Thread was interrupted!?!?. Continuing operation..");
			}
		}
		logger.info("Exiting! Run variable set to false.");
	}

	public boolean includeScreendump() {

		if (--intervalsRatio == 0) {
			intervalsRatio = screendumpInterval / communicationInterval;
			return true;
		}
		return false;
	}

	public MessagePump getPump() {
		return pump;
	}

	public void setPump(MessagePump pump) {
		this.pump = pump;
	}

	/**
	 * Updates the DB schema to the latest version.
	 */
	private final void updateDbSchema() {
		SchemaUpdater schemaUpdater = new SchemaUpdater(SQL_PKG, null);
		schemaUpdater.updateSchema((HibernateTemplate) ctx
				.getBean("hibernateTemplate"));
	}
}
