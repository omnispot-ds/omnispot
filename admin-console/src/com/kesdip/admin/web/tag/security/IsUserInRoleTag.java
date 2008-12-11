/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.tag.security;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

import com.kesdip.common.util.StringUtils;

/**
 * Renders its contents only if the user has the given roles.
 * <p>
 * The tag has the ability to check a set of roles in conjuction or disjunction.
 * </p>
 * 
 * @author gerogias
 */
public class IsUserInRoleTag extends ConditionalTagSupport {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Set of roles when checking in conjuction.
	 */
	private Set<String> allRoles = null;

	/**
	 * Set of roles when checking in disjunction.
	 */
	private Set<String> oneOfRoles = null;

	/**
	 * Perform validation of roles.
	 * 
	 * @throws JspTagException
	 *             if either none or both of the public properties have been
	 *             set.
	 */
	@Override
	protected boolean condition() throws JspTagException {
		if (allRoles == null && oneOfRoles == null) {
			throw new JspTagException("One of the parameters needs to be set");
		}
		if (allRoles != null && oneOfRoles != null) {
			throw new JspTagException("You cannot set both parameters");
		}

		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		if (oneOfRoles != null) {
			for (String role : oneOfRoles) {
				if (request.isUserInRole(role)) {
					return true;
				}
			}
		} else {
			for (String role : allRoles) {
				if (!request.isUserInRole(role)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @param roleString
	 *            the roles to set
	 */
	public void setOneOf(String roleString) {
		if (StringUtils.isEmpty(roleString)) {
			return;
		}
		String[] roles = roleString.split("\\,");
		oneOfRoles = new HashSet<String>();
		for (String role : roles) {
			oneOfRoles.add(role.trim());
		}
	}

	/**
	 * @param roleString
	 *            the roles to set
	 */
	public void setAll(String roleString) {
		if (StringUtils.isEmpty(roleString)) {
			return;
		}
		String[] roles = roleString.split("\\,");
		allRoles = new HashSet<String>();
		for (String role : roles) {
			allRoles.add(role.trim());
		}
	}
}
