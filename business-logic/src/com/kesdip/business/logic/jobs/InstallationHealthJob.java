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
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.constenum.IInstallationStatus;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.StatusEntry;
import com.kesdip.business.logic.BaseLogic;
import com.kesdip.business.logic.InstallationLogic;
import com.kesdip.common.util.DateUtils;

/**
 * Scans the {@link StatusEntry} table for all {@link Installation}s and marks
 * as down all those which have not been updated for {@link #minutesInactive}
 * mins.
 * 
 * @author gerogias
 */
public class InstallationHealthJob extends BaseLogic {

	// private final String SQL = "SELECT i.id, se.last_update \n"
	// + "FROM Installation i INNER JOIN \n"
	// + "(SELECT s.installation_id AS inst_id, s.status_date AS last_update \n"
	// + "FROM Status_Entry s INNER JOIN \n"
	// +
	// "(SELECT installation_id AS inst_id, MAX(status_date) AS last_update \n"
	// + "FROM Status_Entry \n" + "GROUP BY installation_id \n"
	// + ") AS s1 \n" + "ON s.last_update = s1.last_update \n"
	// + "AND s.inst_id = l.inst_id \n" + ") AS se \n"
	// + "ON i.id = se.inst_id \n" + "AND se.last_update < ?";

	private final String SQL = "SELECT i.id, i.uuid, se.status_date \n"
			+ "FROM Installation i INNER JOIN Status_Entry se \n"
			+ "ON i.id = se.installation_id " 
			+ "WHERE se.status_date < ? "
			+ "AND i.status <> " + IInstallationStatus.MACHINE_DOWN
			+ " " + "AND se.status_date >= ALL (SELECT s.status_date \n"
			+ "FROM Status_Entry s \n" + "WHERE s.installation_id = i.id)";

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(InstallationHealthJob.class);

	/**
	 * Number of minutes after which the installation is considered inactive.
	 */
	private int minutesInactive = 5;

	/**
	 * Executes the job's logic.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public void execute() {

		Date dateInThePast = DateUtils.addMinutes(new Date(), -minutesInactive);

		logger.debug("Executing InstallationHealthJob");

		SQLQuery query = (SQLQuery) getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(SQL).setTimestamp(0,
						dateInThePast);
		List<Object[]> results = query.list();
		if (!results.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Found " + results.size()
						+ " non-responsive installations");
			}
			updateInstallationStatuses(results);
		} else {
			logger.info("All installations are up-to-date");
		}
	}

	/**
	 * Iterate the list and update the statuses for all installations.
	 * 
	 * @param results
	 *            the list of ids
	 */
	private final void updateInstallationStatuses(List<Object[]> results) {
		InstallationLogic logic = getLogicFactory().getInstallationLogic();
		for (Object[] row : results) {
			if (logger.isTraceEnabled()) {
				logger.trace("Updating status of " + row[1]);
			}
			logic.updateInstallationStatus((String) row[1],
					IInstallationStatus.MACHINE_DOWN);
		}
	}

	/**
	 * @return the minutesInactive
	 */
	public int getMinutesInactive() {
		return minutesInactive;
	}

	/**
	 * @param minutesInactive
	 *            the minutesInactive to set
	 */
	public void setMinutesInactive(int minutesInactive) {
		this.minutesInactive = minutesInactive;
	}

}
