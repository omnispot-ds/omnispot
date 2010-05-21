package com.kesdip.bootstrap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.bootstrap.communication.ProtocolHandler;
import com.kesdip.business.util.schema.SchemaUpdater;

/**
 * Entry-point class for the bootstrap application.
 * 
 * @author gerogias
 */
public class Manager extends Thread {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Manager.class);

	/**
	 * The package containing schema update SQL scripts.
	 */
	public static final String SQL_PKG = "com/kesdip/bootstrap/schema/";

	/**
	 * Private constructor.
	 */
	private Manager() {
		// do nothing
	}

	/**
	 * The list of supported bootstrap schema versions.
	 */
	private final String[] VERSIONS = { "1.0", "1.1", "1.2" };

	private boolean run = true;

	/**
	 * Interval in millis for heart-beat messages.
	 */
	private long communicationInterval;

	/**
	 * Interval in millis for screendumps.
	 */
	private long screendumpInterval;

	/**
	 * Calculated ratio for screendumps (commInt/dumpInt).
	 */
	private long intervalsRatio;

	/**
	 * The server's URL.
	 */
	private String serverURL = null;

	/**
	 * The internal message pump.
	 */
	private MessagePump pump;

	/**
	 * The bootstrap's application context.
	 */
	private ApplicationContext applicationContext;

	/**
	 * Main method.
	 * 
	 * @param args
	 *            ignored
	 */
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

		applicationContext = new ClassPathXmlApplicationContext("bootstrapContext.xml");

		// update DB before starting message pump
		updateDbSchema();

		MessagePump pump = new MessagePump();
		pump.start();
		setPump(pump);
	}

	@Override
	public void run() {
		init();

//		int exceptioncount = 0;
//		long firstExceptionTimeStamp = 0;
		while (run) {
			try {
				Thread.sleep(communicationInterval);
				ProtocolHandler comm = (ProtocolHandler) applicationContext
						.getBean("ProtocolHandler");
				comm.setManager(this);
				try {
					comm.performRequest();
				} catch (Exception e) {
					logger.error(e);
					// "restart on 5 exceptions" feature can cause problems when network is down
//					exceptioncount++;
//					if (firstExceptionTimeStamp == 0) {
//						firstExceptionTimeStamp = new Date().getTime();
//					}
//					if (exceptioncount == 5) {
//						if (new Date().getTime() - firstExceptionTimeStamp < 1800 * 1000) {
//							logger
//									.error("Exception count equal to 5 in less than half an hour. Restarting...");
//							
//							// windows will restart it hopefully...
//							System.exit(0);
//						} else {
//							exceptioncount = 0;
//							firstExceptionTimeStamp = 0;
//						}
//					}
//					logger.info("Continuing operation...");
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

	private void setPump(MessagePump pump) {
		this.pump = pump;
	}

	/**
	 * Updates the DB schema to the latest version.
	 */
	private final void updateDbSchema() {
		SchemaUpdater schemaUpdater = new SchemaUpdater(SQL_PKG, null, VERSIONS);
		schemaUpdater.updateSchema((HibernateTemplate) applicationContext
				.getBean("hibernateTemplate"));
	}

	// *** Getters and setters below are for dynamic configuration only ***  
	
	/**
	 * @return the communicationInterval
	 */
	public long getCommunicationInterval() {
		return communicationInterval;
	}

	/**
	 * @param communicationInterval the communicationInterval to set
	 */
	public void setCommunicationInterval(long communicationInterval) {
		this.communicationInterval = communicationInterval;
	}

	/**
	 * @return the screendumpInterval
	 */
	public long getScreendumpInterval() {
		return screendumpInterval;
	}

	/**
	 * @param screendumpInterval the screendumpInterval to set
	 */
	public void setScreendumpInterval(long screendumpInterval) {
		this.screendumpInterval = screendumpInterval;
	}

	/**
	 * @return the serverURL
	 */
	public String getServerURL() {
		return serverURL;
	}

	/**
	 * @param serverURL the serverURL to set
	 */
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
