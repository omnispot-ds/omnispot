package com.kesdip.business.logic;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.exception.ValidationException;

/**
 * Site-related logic.
 * 
 * @author pkattoul
 */
public class SiteLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(SiteLogic.class);

	/**
	 * @return List all active sites
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Site> getAllSites() {
		return getHibernateTemplate().find(
				"select c from " + SiteLogic.class.getName() + " c "
						+ "where c.active = true");
	}

	/**
	 * Create a new Site in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Site create(Site object) throws ValidationException {

		validate(object, "create");
		logger.debug("Creating Site");
		// set affiliation
		Customer dbCustomer = null;
		if (object.getCustomer() != null) {
			dbCustomer = getLogicFactory().getCustomerLogic().getInstance(
					object.getCustomer());
			object.setCustomer(dbCustomer);
		}
		object.setActive(true);
		getHibernateTemplate().save(object);
		// set affiliation
		if (object.getCustomer() != null) {
			dbCustomer.getSites().add(object);
			getHibernateTemplate().update(dbCustomer);
		}
		return object;
	}

	/**
	 * Update a Site in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Site update(Site object) throws ValidationException {

		validate(object, "edit");
		if (logger.isDebugEnabled()) {
			logger.debug("Updating Site " + object.getId());
		}
		Site dbSite = getInstance(object);
		dbSite.setName(object.getName());
		dbSite.setComments(object.getComments());
		getHibernateTemplate().update(dbSite);
		return object;
	}

	/**
	 * Marks a Site as deleted. Also deletes all its Installations.
	 * 
	 * @param object
	 *            the DTO
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(Site object) throws ValidationException {

		validate(object, "delete");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Site " + object.getName());
		}
		Site dbInstance = getInstance(object);
		dbInstance.setActive(false);
		getHibernateTemplate().update(dbInstance);

		InstallationLogic installationLogic = getLogicFactory().getInstallationLogic();
		for (Installation installation : dbInstance.getInstallations()) {
			installationLogic.delete(installation);
		}
	}

	/**
	 * Return the DB instance from the DTO.
	 * 
	 * @return Site the object or <code>null</code>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Site getInstance(Site dto) {
		if (dto == null || dto.getId() == null) {
			logger.info("DTO is null");
			return null;
		}
		List<Site> sites = getHibernateTemplate().find(
				"select s from " + Site.class.getName() + " s "
						+ "left join fetch s.customer " + "where s = ?", dto);
		return sites.isEmpty() ? null : sites.iterator().next();
	}

}
