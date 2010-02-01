/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jun 1, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic.jobs;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.StatusEntry;
import com.kesdip.business.logic.BaseLogic;
import com.kesdip.common.util.DateUtils;

/**
 * Removes all entries from the {@link StatusEntry} table which are more than
 * {@link #daysInThePast} days old.
 * 
 * @author gerogias
 */
public class StatusEntryCleanupJob extends BaseLogic {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(StatusEntryCleanupJob.class);

	/**
	 * Number of days in the past after which to delete entries.
	 */
	private int daysInThePast = 7;

	/**
	 * @return the daysInThePast
	 */
	public int getDaysInThePast() {
		return daysInThePast;
	}

	/**
	 * @param daysInThePast
	 *            the daysInThePast to set
	 */
	public void setDaysInThePast(int daysInThePast) {
		this.daysInThePast = daysInThePast;
	}

	/**
	 * Executes the job's logic.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void execute() {
		Date dateInThePast = DateUtils.addDays(new Date(), -daysInThePast);
		if (logger.isDebugEnabled()) {
			logger
					.debug("Deleting all StatusEntry values before "
							+ dateInThePast);
		}
		SQLQuery query = (SQLQuery) getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(
						"DELETE FROM STATUS_ENTRY WHERE STATUS_DATE < ?")
				.setDate(0, dateInThePast);
		int deleted = query.executeUpdate();
		if (logger.isInfoEnabled()) {
			logger.info("Deleted " + deleted + " StatusEntry values");
		}
	}
}
