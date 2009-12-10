package com.kesdip.bootstrap.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.common.util.DBUtils;

/**
 * Base class for all message implementation classes.
 * 
 * @author gerogias
 */
public abstract class Message implements IMessage {

	/**
	 * The active {@link HibernateTemplate} instance.
	 */
	private HibernateTemplate hibernateTemplate = null;
	
	
	private void actionReport(short status) throws Exception {
		if (getActionId() == null) {
			return;
		}
		// messages with OK status not handled in the pump thread
		if (!isOKHandledInPumpThread() && status == IActionStatusEnum.OK) {
			return;
		}

		Connection c = null;
		try {
			c = DBUtils.getConnection();

			PreparedStatement ps = c.prepareStatement("UPDATE ACTION "
					+ "SET STATUS=? WHERE ACTION_ID=?");
			ps.setShort(1, status);
			ps.setString(2, getActionId());

			ps.executeUpdate();
			ps.close();

			c.commit();
		} catch (Exception e) {
			if (c != null) {
				try {
					c.rollback();
				} catch (SQLException sqle) {
					// do nothing
				}
			}
			throw e;
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					// do nothing
				}
			}
		}
	}

	public void handle() throws Exception {
		actionReport(IActionStatusEnum.IN_PROGRESS);
		boolean failed = false;
		Exception exception = null;
		try {
			process();
		} catch (Exception e) {
			failed = true;
			exception = e;
		}
		if (failed) {
			actionReport(IActionStatusEnum.FAILED);
			throw exception;
		}
		actionReport(IActionStatusEnum.OK);
	}

	/**
	 * Template method for descendants to execute their logic.
	 * 
	 * @throws Exception
	 *             on error
	 */
	public abstract void process() throws Exception;

	/**
	 * @return String the action's unique id
	 */
	public abstract String getActionId();

	/**
	 * Signals whether the OK status update is handled inside the pump thread or not.
	 * In some messages taking a lot of time to complete (e.g. {@link DeployMessage}), the 
	 * message must not be handled as OK inside the oump, but at a later time. 
	 * 
	 * @return <code>true</code> by default
	 */
	protected boolean isOKHandledInPumpThread() {
		return true;
	}

	/**
	 * @return the hibernateTemplate
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
}
