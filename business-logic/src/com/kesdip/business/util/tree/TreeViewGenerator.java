/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.logic.BaseLogic;

/**
 * Utility bean offering all possible tree views.
 * 
 * @author gerogias
 */
public class TreeViewGenerator extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(TreeViewGenerator.class);

	/**
	 * Default constructor.
	 */
	public TreeViewGenerator() {
		// do nothing
	}

	/**
	 * Get a tree view, starting from the {@link User}.
	 * 
	 * @return List a list of nodes
	 */
	@Transactional(readOnly = true)
	public List<TreeNode> getUsers() {
		logger.trace("Returning flat user tree");
		TreeNodeFactory factory = new TreeNodeFactory(getHibernateTemplate());
		return factory.getTree(User.class);
	}

	/**
	 * Get a tree view, with mixed {@link User}s and {@link Customer}s.
	 * 
	 * @return List a list of nodes
	 */
	@Transactional(readOnly = true)
	public List<TreeNode> getUsersCustomers() {
		logger.trace("Returning users and customers tree");
		TreeNodeFactory factory = new TreeNodeFactory(getHibernateTemplate());
		// customers
		List<Customer> customers = getLogicFactory().getCustomerLogic().getAllCustomers();
		// non-affiliated users
		List<User> plainUsers = getLogicFactory().getUserLogic().getNonAffiliatedUsers();
		List<Serializable> objects = new ArrayList<Serializable>();
		objects.addAll(customers);
		objects.addAll(plainUsers);
		return factory.getTree(objects);
	}
	
	/**
	 * Get a tree view starting with {@link Customer}s.
	 * 
	 * @return List a list of nodes
	 */
	@Transactional(readOnly = true)
	public List<TreeNode> getCustomers() {
		logger.trace("Returning customers tree");
		TreeNodeFactory factory = new TreeNodeFactory(getHibernateTemplate());
		return factory.getTree(Customer.class);
	}
}