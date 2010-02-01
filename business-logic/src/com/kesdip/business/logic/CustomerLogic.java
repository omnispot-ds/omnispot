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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.exception.ValidationException;

/**
 * Customer-related logic.
 * 
 * @author gerogias
 */
public class CustomerLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(CustomerLogic.class);

	/**
	 * @return List all active customers
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Customer> getAllCustomers() {
		return getHibernateTemplate().find(
				"select c from " + Customer.class.getName() + " c "
						+ "where c.active = true");
	}

	/**
	 * Create a new Customer in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Customer create(Customer object) throws ValidationException {

		validate(object, "create");
		logger.debug("Creating Customer");
		object.setActive(true);
		object.setId((Long) getHibernateTemplate().save(object));
		return object;
	}

	/**
	 * Update a Customer in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Customer update(Customer object) throws ValidationException {

		validate(object, "edit");
		if (logger.isDebugEnabled()) {
			logger.debug("Updating Customer " + object.getId());
		}
		Customer dbCustomer = getInstance(object);
		dbCustomer.setName(object.getName());
		dbCustomer.setComments(object.getComments());
		getHibernateTemplate().update(dbCustomer);
		return object;
	}

	/**
	 * Marks a Customer as deleted.
	 * Also deletes all its users.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(Customer object) throws ValidationException {

		validate(object, "delete");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting Customer " + object.getName());
		}
		Customer dbInstance = getInstance(object);
		dbInstance.setActive(false);
		getHibernateTemplate().update(dbInstance);
		
		UserLogic userLogic = getLogicFactory().getUserLogic();
		for (User user : dbInstance.getUsers()) {
			userLogic.delete(user);
		}
	}

	/**
	 * Return the DB instance from the DTO.
	 * 
	 * @return Customer the object or <code>null</code>
	 */
	@Transactional(readOnly = true)
	public Customer getInstance(Customer dto) {
		if (dto == null || dto.getId() == null) {
			logger.info("DTO is null");
			return null;
		}
		Customer customer = (Customer) getHibernateTemplate().get(
				Customer.class, dto.getId());
		return customer;
	}

}
