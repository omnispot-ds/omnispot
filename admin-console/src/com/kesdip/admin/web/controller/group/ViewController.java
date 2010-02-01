/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.group;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.logic.GroupLogic;

/**
 * Controller for the {@link InstallationGroup} viewing form.
 * 
 * @author gerogias
 */
public class ViewController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates the form-backing object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		InstallationGroup group = new InstallationGroup();
		group.setId(Long.valueOf(request.getParameter("id")));

		GroupLogic logic = getLogicFactory().getGroupLogic();
		InstallationGroup dbGroup = logic.getInstance(group);
		System.out.println("dbgroup has installations: " + dbGroup.getInstallations().size());
		setCurrentObject(request, dbGroup);
		return dbGroup;
	}
}
