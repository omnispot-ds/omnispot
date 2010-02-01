/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.user;

import java.util.List;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.AccessRight;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.validation.BaseValidator;

/**
 * Base class for user validators.
 * 
 * @author gerogias
 */
public abstract class BaseUserValidator extends BaseValidator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(BaseUserValidator.class);

	/**
	 * Checks if the user has the given role.
	 * 
	 * @param user
	 *            the user
	 * @param role
	 *            the role
	 * @return boolean if the role is present
	 */
	protected final boolean hasRole(User user, String role) {
		if (user == null || user.getRights() == null) {
			logger.debug("User is null or has null roles");
			return false;
		}
		for (AccessRight tmp : user.getRights()) {
			if (role.equals(tmp.getName())) {
				if (logger.isDebugEnabled()) {
					logger.debug("User " + user.getUsername() + " has role "
							+ role);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all the users having this access right.
	 * 
	 * @param role
	 *            the role
	 * @return List the users, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	protected final List<User> getUsersWithAccessRight(String role) {
		return getHibernateTemplate().find(
				"select u from " + User.class.getName() + " u "
						+ "inner join u.rights r " + "where r.name = ?", role);
	}

}
