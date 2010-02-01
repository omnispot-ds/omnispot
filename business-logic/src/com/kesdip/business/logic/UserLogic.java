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
 * User-related logic.
 * 
 * @author gerogias
 */
public class UserLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(UserLogic.class);

	/**
	 * Performs user login. Checks the user's username and password against the
	 * DB.
	 * 
	 * @param user
	 *            the user to check
	 * @return User the DB user object
	 * @throws ValidationException
	 *             if fields were missing or the user could not be authenticated
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public User doLogin(User user) throws ValidationException {

		validate(user, "doLogin");
		if (logger.isDebugEnabled()) {
			logger.debug("Login for " + user.getUsername());
		}
		List<User> users = getHibernateTemplate().find(
				"from " + User.class.getName() + " u where u.username = ? "
						+ "and u.password = ? ",
				new Object[] { user.getUsername(), user.getPassword() });
		if (users.isEmpty()) {
			logger.debug("Login failed");
			ValidationException exc = new ValidationException();
			exc.addError("error.invalid.credentials");
			throw exc;
		}
		// there should be only 1
		return users.get(0);
	}

	/**
	 * Create a new User in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public User create(User object) throws ValidationException {

		validate(object, "create");
		logger.debug("Creating User");
		// set affiliation
		Customer dbCustomer = null;
		if (object.getAffiliation() != null) {
			dbCustomer = getLogicFactory().getCustomerLogic()
					.getInstance(object.getAffiliation());
			object.setAffiliation(dbCustomer);
		}
		getHibernateTemplate().save(object);
		// set affiliation
		if (object.getAffiliation() != null) {
			dbCustomer.getUsers().add(object);
			getHibernateTemplate().update(dbCustomer);
		}
		return object;
	}

	/**
	 * Update a User in the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public User update(User object) throws ValidationException {

		validate(object, "edit");
		if (logger.isDebugEnabled()) {
			logger.debug("Updating User " + object.getUsername());
		}
		User dbUser = getInstance(object);
		dbUser.setFirstName(object.getFirstName());
		dbUser.setLastName(object.getLastName());
		dbUser.setPassword(object.getPassword());
		dbUser.setUsername(object.getUsername());
		dbUser.setRights(object.getRights());
		getHibernateTemplate().update(dbUser);
		return object;
	}

	/**
	 * Delete a User from the database.
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object, wrapped
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void delete(User object) throws ValidationException {

		validate(object, "delete");
		if (logger.isDebugEnabled()) {
			logger.debug("Deleting User " + object.getUsername());
		}
		User dbInstance = getInstance(object);
		getHibernateTemplate().delete(dbInstance);
	}

	/**
	 * Return the DB instance from the DTO.
	 * 
	 * @return User the object or <code>null</code>
	 */
	@Transactional(readOnly = true)
	public User getInstance(User dto) {
		if (dto == null || dto.getUsername() == null) {
			logger.info("DTO is null");
			return null;
		}
		User user = (User) getHibernateTemplate().get(User.class,
				dto.getUsername());
		return user;
	}

	/**
	 * @return List all non-affiliated users (users not associated with a
	 *         customer)
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<User> getNonAffiliatedUsers() {
		return getHibernateTemplate().find(
				"select u from " + User.class.getName() + " u "
						+ "where u.affiliation = null");
	}
}
