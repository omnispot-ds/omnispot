/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.business.constenum.IInstallationStatus;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.common.util.BeanUtils;

/**
 * Utility class to generate {@link TreeNode} objects from domain classes.
 * <p>
 * The class can start from any object type in the graph and traverse all its
 * children recursively. To avoid creating circular references, each tree is
 * created within a single session, avoiding to process the same class type
 * twice. The same factory instance may be re-used, but only after calling
 * {@link #reset()}.
 * </p>
 * 
 * @author gerogias
 */
public class TreeNodeFactory {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(TreeNodeFactory.class);

	/**
	 * The set of visited types. Each entry has the class name and the depth in
	 * the tree where it was first encountered.
	 */
	@SuppressWarnings("unchecked")
	private Map<Class, Integer> visitedTypes = null;

	/**
	 * The template to use.
	 */
	private HibernateTemplate template = null;

	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unchecked")
	public TreeNodeFactory(HibernateTemplate template) {
		visitedTypes = new HashMap<Class, Integer>();
		this.template = template;
	}

	/**
	 * Resets the internal state.
	 */
	public void reset() {
		visitedTypes.clear();
	}

	/**
	 * Returns an exhaustive graph of the domain object instances beginning from
	 * the given type.
	 * 
	 * @param clazz
	 *            the type
	 * @return List the resulting nodes, never <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public <T> List<TreeNode> getTree(Class<T> clazz) {
		List<T> queryResults = template.find("from " + clazz.getName());
		if (logger.isTraceEnabled()) {
			logger.trace("Found " + queryResults.size() + " objects");
		}
		List<TreeNode> results = new ArrayList<TreeNode>();
		TreeNode node = null;
		for (T obj : queryResults) {
			node = createNodeInternal(obj);
			if (node != null) {
				results.add(node);
			}
		}
		return results;
	}

	/**
	 * Returns an exhaustive graph of the domain object instances beginning from
	 * the given objects.
	 * 
	 * @param objects
	 *            the instances
	 * @return List the resulting nodes, never <code>null</code>
	 */
	public <T> List<TreeNode> getTree(List<T> objects) {
		List<TreeNode> results = new ArrayList<TreeNode>();
		TreeNode node = null;
		for (T obj : objects) {
			node = createNodeInternal(obj);
			if (node != null) {
				results.add(node);
			}
		}
		return results;
	}

	/**
	 * Utility method to delegate to the appropriate method.
	 * 
	 * @param obj
	 *            the object to process
	 * @return the created node or <code>null</code>
	 */
	final TreeNode createNodeInternal(Object obj) {
		int depth = 0;
		if (obj instanceof User) {
			return createNode((User) obj, depth);
		} else if (obj instanceof Customer) {
			return createNode((Customer) obj, depth);
		}
		return null;
	}

	/**
	 * Creates a node from a customer and its children.
	 * 
	 * @param customer
	 *            the object
	 * @param depth
	 *            the current depth
	 * 
	 * @return TreeNode the created node or <code>null</code> if the argument
	 *         was <code>null</code>
	 */
	final TreeNode createNode(Customer customer, int depth) {
		if (customer == null) {
			logger.trace("Customer is null");
			return null;
		}
		if (!classIsAllowed(Customer.class, depth)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping Customer for depth " + depth);
			}
			return null;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Processing Customer " + customer.getId());
		}
		visitedTypes.put(Customer.class, depth);
		TreeNode customerNode = new TreeNode(customer.getId(), BeanUtils
				.getClassName(customer.getClass()), customer.getName());
		customerNode.setDisabled(!customer.isActive());
		TreeNode temp = null;
		// users
		for (User user : customer.getUsers()) {
			temp = createNode(user, depth + 1);
			if (temp != null) {
				customerNode.addChild(temp);
			}
		}
		// groups
		for (InstallationGroup group : customer.getGroups()) {
			temp = createNode(group, depth + 1);
			if (temp != null) {
				customerNode.addChild(temp);
			}
		}
		// sites
		for (Site site : customer.getSites()) {
			temp = createNode(site, depth + 1);
			if (temp != null) {
				customerNode.addChild(temp);
			}
		}

		return customerNode;
	}

	/**
	 * Creates a node from a {@link User}.
	 * 
	 * @param user
	 *            the user
	 * @param depth
	 *            the current depth
	 * @return TreeNode the node or <code>null</code>
	 */
	final TreeNode createNode(User user, int depth) {
		if (user == null) {
			logger.trace("User is null");
			return null;
		}
		if (!classIsAllowed(User.class, depth)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping User for depth " + depth);
			}
			return null;
		}
		visitedTypes.put(User.class, depth);
		if (logger.isTraceEnabled()) {
			logger.trace("Processing User " + user.getUsername());
		}
		TreeNode node = new TreeNode(user.getUsername(), BeanUtils
				.getClassName(user.getClass()), user.getLastName() + ' '
				+ user.getFirstName());
		node.setDisabled(false);
		return node;
	}

	/**
	 * Creates a node from an {@link InstallationGroup}.
	 * 
	 * @param group
	 *            the group
	 * @param depth
	 *            the current depth
	 * @return TreeNode the node or <code>null</code>
	 */
	final TreeNode createNode(InstallationGroup group, int depth) {
		if (group == null) {
			logger.trace("InstallationGroup is null");
			return null;
		}
		if (!classIsAllowed(InstallationGroup.class, depth)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping InstallationGroup for depth " + depth);
			}
			return null;
		}
		visitedTypes.put(InstallationGroup.class, depth);
		if (logger.isTraceEnabled()) {
			logger.trace("Processing InstallationGroup " + group.getName());
		}
		TreeNode node = new TreeNode(group.getId(), BeanUtils
				.getClassName(group.getClass()), group.getName());
		node.setDisabled(false);
		node.setStatus(getGroupStatus(group));
		// installations
		TreeNode temp = null;
		for (Installation installation : group.getInstallations()) {
			temp = createNode(installation, depth + 1);
			if (temp != null) {
				//add site information on installation
				temp.setName(installation.getName() + " (" + installation.getSite().getName() + ")");
				node.addChild(temp);
			}
		}
		return node;
	}

	/**
	 * Creates a node from a {@link Site}.
	 * 
	 * @param site
	 *            the site
	 * @param depth
	 *            the current depth
	 * @return TreeNode the node or <code>null</code>
	 */
	final TreeNode createNode(Site site, int depth) {
		if (site == null) {
			logger.trace("Site is null");
			return null;
		}
		if (!classIsAllowed(Site.class, depth)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping Site for depth " + depth);
			}
			return null;
		}
		visitedTypes.put(Site.class, depth);
		if (logger.isTraceEnabled()) {
			logger.trace("Processing Site " + site.getName());
		}
		TreeNode node = new TreeNode(site.getId(), BeanUtils
				.getClassName(site.getClass()), site.getName());
		node.setDisabled(!site.isActive());
		node.setStatus(getSiteStatus(site));
		// installations
		TreeNode temp = null;
		for (Installation installation : site.getInstallations()) {
			temp = createNode(installation, depth + 1);
			if (temp != null) {
				node.addChild(temp);
			}
		}
		return node;
	}

	/**
	 * Creates a node from an {@link Installation}.
	 * 
	 * @param installation
	 *            the group
	 * @param depth
	 *            the current depth
	 * @return TreeNode the node or <code>null</code>
	 */
	final TreeNode createNode(Installation installation, int depth) {
		if (installation == null) {
			logger.trace("Installation is null");
			return null;
		}
		if (!classIsAllowed(Installation.class, depth)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping Installation for depth " + depth);
			}
			return null;
		}
		visitedTypes.put(Installation.class, depth);
		if (logger.isTraceEnabled()) {
			logger.trace("Processing Installation " + installation.getName());
		}
		TreeNode node = new TreeNode(installation.getId(), BeanUtils
				.getClassName(installation.getClass()), installation.getName());
		node.setDisabled(!installation.isActive());
		node.setStatus(getInstallationStatus(installation));
		return node;
	}

	/**
	 * Returns a status string for the given {@link Installation}.
	 * 
	 * @param installation
	 *            the object
	 * @return String the status
	 */
	private final String getInstallationStatus(Installation installation) {
		if (installation.getCurrentStatus() == IInstallationStatus.OK) {
			return "ok";
		} else if (installation.getCurrentStatus() == IInstallationStatus.PLAYER_DOWN) {
			return "nok";
		} else if (installation.getCurrentStatus() == IInstallationStatus.MACHINE_DOWN) {
			return "down";
		}
		return "";
	}
	
	/**
	 * Returns a status string for the given {@link Site}.
	 * 
	 * @param site
	 *            the object
	 * @return String the status
	 */
	private final String getSiteStatus(Site site) {
		if (site.getCurrentStatus() == null) {
			return "";
		} else if (site.getCurrentStatus() == IInstallationStatus.OK) {
			return "ok";
		} else if (site.getCurrentStatus() == IInstallationStatus.PLAYER_DOWN) {
			return "nok";
		} else if (site.getCurrentStatus() == IInstallationStatus.MACHINE_DOWN) {
			return "down";
		}
		return "";
	}
	
	/**
	 * Returns a status string for the given {@link InstallationGroup}.
	 * 
	 * @param group
	 *            the object
	 * @return String the status
	 */
	private final String getGroupStatus(InstallationGroup group) {
		if (group.getCurrentStatus() == null) {
			return "";
		} else if (group.getCurrentStatus() == IInstallationStatus.OK) {
			return "ok";
		} else if (group.getCurrentStatus() == IInstallationStatus.PLAYER_DOWN) {
			return "nok";
		} else if (group.getCurrentStatus() == IInstallationStatus.MACHINE_DOWN) {
			return "down";
		}
		return "";
		
	}

	/**
	 * Checks if the class can be included in the tree at this depth or not.
	 * <p>
	 * Checks if the class already exists in the map of visited classes. If the
	 * class does not exist in the map, the addition is allowed. If the class
	 * exists with a depth greater or equal to the current one, the additiona is
	 * allowed.
	 * </p>
	 * 
	 * @param clazz
	 *            the class to check
	 * @param depth
	 *            the current depth
	 * @return boolean <code>true</code> if it can be included
	 */
	@SuppressWarnings("unchecked")
	private final boolean classIsAllowed(Class clazz, int depth) {
		if (!this.visitedTypes.containsKey(clazz)) {
			return true;
		}
		Integer previousDepth = visitedTypes.get(clazz);
		return previousDepth >= depth;
	}
}
