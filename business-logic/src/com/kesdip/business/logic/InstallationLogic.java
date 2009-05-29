/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.business.logic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.ViewPrintScreenBean;
import com.kesdip.business.constenum.IInstallationStatus;
import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Site;
import com.kesdip.business.domain.generated.StatusEntry;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.common.util.DateUtils;

/**
 * Installation-related logic.
 * 
 * @author sgerogia
 */
public class InstallationLogic extends BaseLogic {

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
						+ "left join fetch s.customer "
						+ "left join fetch i.deployments d " + "where i = ? ",
				dto);
		Installation installation = null;
		if (!installations.isEmpty()) {
			installation = installations.get(0);
			// prinstscreen
			ViewPrintScreenBean bean = new ViewPrintScreenBean();
			bean.setInstallation(installation);
			PrintScreenLogic psLogic = getLogicFactory().getPrintScreenLogic();
			installation.setPrintScreen(psLogic.getPrintScreens(bean)
					.getPrintScreens().iterator().next());
		}
		return installation;
	}

	/**
	 * Create a new Installation in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Installation create(Installation object) throws ValidationException {

		validate(object, "create");
		logger.debug("Creating Installation");
		// set Site
		Site dbSite = getLogicFactory().getSiteLogic().getInstance(
				object.getSite());
		object.setSite(dbSite);
		object.setActive(true);
		String uuid = generateUuid();
		object.setUuid(uuid);
		object.setCurrentStatus(IInstallationStatus.MACHINE_DOWN);
		object.setId((Long) getHibernateTemplate().save(object));
		// TODO update keystore

		return object;
	}

	/**
	 * Update an Installation in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Installation update(Installation object) throws ValidationException {

		validate(object, "edit");
		if (logger.isDebugEnabled()) {
			logger.debug("Updating Site " + object.getId());
		}
		Installation dbInstallation = getInstance(object);
		dbInstallation.setName(object.getName());
		dbInstallation.setScreenType(object.getScreenType());
		dbInstallation.setComments(object.getComments());
		getHibernateTemplate().update(dbInstallation);
		return object;
	}

	/**
	 * Marks an Installation as deleted. Also deletes its key from the keystore.
	 * 
	 * @param object
	 *            the DTO
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(Installation object) throws ValidationException {

		validate(object, "delete");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Installation " + object.getId());
		}
		Installation dbInstance = getInstance(object);
		dbInstance.setActive(false);
		getHibernateTemplate().update(dbInstance);
		// TODO Delete key from keystore
	}

	/**
	 * @param site
	 *            the site to look for
	 * @return int the child installations
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
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
	 * @return Set the child installations or <code>null</code> if the argument
	 *         was <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
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

	/**
	 * Returns the installation with this UUId.
	 * 
	 * @param uuid
	 *            the uuid
	 * @return Installation the instance or <code>null</code>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Installation getInstallationByUuid(String uuid) {
		List<Installation> results = getHibernateTemplate().find(
				"select i from " + Installation.class.getName() + " i "
						+ "where i.uuid = ? ", uuid);
		return !results.isEmpty() ? results.get(0) : null;
	}

	/**
	 * Updates the status of the installation. Also adds/updates an entry in the
	 * {@link StatusEntry} table.
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void updateInstallationStatus(Installation dto, short status) {
		if (logger.isTraceEnabled()) {
			logger.trace("Updating Installation " + dto.getId()
					+ " with status " + status);
		}
		// update object
		Installation dbInstallation = (Installation) getHibernateTemplate()
				.get(Installation.class, dto.getId());
		dbInstallation.setCurrentStatus(status);
		getHibernateTemplate().update(dbInstallation);
		// status entry
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery("select se from " + StatusEntry.class.getName() + " where se.");
	}

	/**
	 * @return String a unique UUID
	 */
	private String generateUuid() {
		String base = UUID.randomUUID().toString();
		String date = new SimpleDateFormat(DateUtils.DATE_FORMAT)
				.format(new Date());
		int salt = new Random(System.currentTimeMillis()).nextInt(100000);
		return base + '_' + salt + '_' + date.replace('/', '-');
	}
}
