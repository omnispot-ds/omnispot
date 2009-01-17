/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.business.logic;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Site;

/**
 * Installation-related logic.
 * 
 * @author sgerogia
 */
public class InstallationLogic extends BaseLogicAction {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(InstallationLogic.class);

	/**
	 * Return the DB instance from the DTO.
	 * 
	 * @return Installation the object or <code>null</code>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Installation getInstance(Installation dto) {
		if (dto == null || dto.getId() == null) {
			logger.info("DTO is null");
			return null;
		}
		List<Installation> installations = getHibernateTemplate().find(
				"select i from " + Installation.class.getName() + " i "
						+ "left join fetch i.site s "
						+ "left join fetch s.customer" + "where i = ?", dto);
		return installations.isEmpty() ? null : installations.iterator().next();
	}

	/**
	 * @param site
	 *            the site to look for
	 * @return int the child installations
	 */
	@SuppressWarnings("unchecked")
	public int getInstallationCount(Site site) {
		if (site == null) {
			logger.info("DTO is null");
			return 0;
		}
		List<Long> results = getHibernateTemplate().find(
				"select count(i) from " + Installation.class.getName() + " i "
						+ "where i.site = ? " + "and i.active = true", site);
		return results.get(0).intValue();
	}

	/**
	 * @param group
	 *            the group to look for
	 * @return int the child installations
	 */
	@SuppressWarnings("unchecked")
	public int getInstallationCount(InstallationGroup group) {
		if (group == null) {
			logger.info("DTO is null");
			return 0;
		}
		List<Long> results = getHibernateTemplate().find(
				"select count(i) from " + Installation.class.getName() + " i "
						+ "inner join i.groups g " + "where g = ? "
						+ "and i.active = true", group);
		return results.get(0).intValue();
	}

	/**
	 * @param customer
	 *            the customer to look for
	 * @return int the child installations
	 */
	@SuppressWarnings("unchecked")
	public int getInstallationCount(Customer customer) {
		if (customer == null) {
			logger.info("DTO is null");
			return 0;
		}
		List<Long> results = getHibernateTemplate().find(
				"select count(i) from " + Installation.class.getName() + " i "
						+ "where i.site.customer = ? " + "and i.active = true",
				customer);
		return results.get(0).intValue();
	}

	/**
	 * @param customer
	 *            the customer to look for
	 * @return Set the child installations or <code>null</code> if the
	 *         argument was <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public List<Installation> getInstallations(Customer customer) {
		if (customer == null) {
			logger.info("DTO is null");
			return null;
		}
		List<Installation> results = getHibernateTemplate().find(
				"select i from " + Installation.class.getName() + " i "
						+ "where i.site.customer = ? " + "and i.active = true",
				customer);
		return results;
	}

}
