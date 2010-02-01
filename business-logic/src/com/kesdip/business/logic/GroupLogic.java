/**
 * 
 */
package com.kesdip.business.logic;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.exception.ValidationException;

/**
 * Installation group related settings.
 * 
 * @author pavlos
 */
public class GroupLogic extends BaseLogic {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(GroupLogic.class);
	
	/**
	 * @return List all active InstallationGroups
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<InstallationGroup> getAllGroups() {
		return getHibernateTemplate().find(
				"select c from " + InstallationGroup.class.getName() + " c "
						+ "where c.active = true");
	}
	
	/**
	 * Create a new InstallationGroup in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public InstallationGroup create(InstallationGroup object) throws ValidationException {

		validate(object, "create");
		logger.debug("Creating Group");
		// set customer
		Customer dbCustomer = null;
		if (object.getCustomer() != null) {
			dbCustomer = getLogicFactory().getCustomerLogic()
					.getInstance(object.getCustomer());
			object.setCustomer(dbCustomer);
		}
		getHibernateTemplate().save(object);
		// set affiliation
		if (object.getCustomer() != null) {
			dbCustomer.getGroups().add(object);
			getHibernateTemplate().update(dbCustomer);
		}
		return object;
	}
	
	/**
	 * Update an InstallationGroup in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public InstallationGroup update(InstallationGroup object) throws ValidationException {

		validate(object, "edit");
		if (logger.isDebugEnabled()) {
			logger.debug("Updating Group " + object.getId());
		}
		InstallationGroup dbGroup = getInstance(object);
		dbGroup.setName(object.getName());
		dbGroup.setComments(object.getComments());
		dbGroup.setInstallations(object.getInstallations());
		getHibernateTemplate().update(dbGroup);
		return object;
	}
	
	/**
	 * Deletes an InstallationGroup
	 * 
	 * @param object
	 *            the DTO
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(InstallationGroup object) throws ValidationException {

		validate(object, "delete");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Group " + object.getName());
		}
		InstallationGroup dbInstance = getInstance(object);
		getHibernateTemplate().delete(dbInstance);
	}
	
	
	/**
	 * Return the DB instance from the DTO.
	 * 
	 * @return InstallationGroup the object or <code>null</code>
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public InstallationGroup getInstance(InstallationGroup dto) {
		if (dto == null || dto.getId() == null) {
			logger.info("DTO is null");
			return null;
		}
		List<InstallationGroup> groups = getHibernateTemplate().find("select g from " 
				+ InstallationGroup.class.getName() + " g "
				+ "left join fetch g.customer "
				+ "where g = ?", dto);
		return groups.isEmpty() ? null : groups.iterator().next();
	}


}
