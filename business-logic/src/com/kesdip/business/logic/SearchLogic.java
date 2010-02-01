/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.QuickSearchBean;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.common.util.StringUtils;

/**
 * Search-related logic.
 * 
 * @author gerogias
 */
public class SearchLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(SearchLogic.class);

	/**
	 * Performs a quick search. Uses the name from the DTO to search for similar
	 * values the following entities:
	 * <ul>
	 * <li>{@link User}</li>
	 * </ul>
	 * 
	 * @param user
	 *            the user to check
	 * @return Serializable the first encountered result or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Serializable quickSearch(QuickSearchBean bean) {

		if (bean == null || StringUtils.isEmpty(bean.getName())) {
			logger.debug("DTO is null or name is empty");
			return null;
		}
		logger.debug("Performing quick search");
		List<Serializable> results = null;
		// user
		results = getHibernateTemplate().find(
				"from " + User.class.getName() + " u where u.lastName like '"
						+ bean.getName() + "%' ");
		if (!results.isEmpty()) {
			logger.debug("Found User");
			return results.get(0);
		}
		// customer
		results = getHibernateTemplate().find(
				"from " + Customer.class.getName() + " u where u.name like '"
						+ bean.getName() + "%' ");
		if (!results.isEmpty()) {
			logger.debug("Found Customer");
			return results.get(0);
		}
		logger.debug("Found nothing");
		return null;
	}
}
