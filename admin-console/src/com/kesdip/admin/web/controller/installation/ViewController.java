/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 23, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.installation;

import java.util.Comparator;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.Deployment;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.logic.InstallationLogic;

/**
 * Controller for the Installation viewing form.
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

		Installation installation = new Installation();
		installation.setId(Long.valueOf(request.getParameter("id")));

		InstallationLogic logic = getLogicFactory().getInstallationLogic();
		Installation dbInstallation = logic.getInstance(installation);
		
		//order deployments by id descending
		TreeSet<Deployment> orderedDeployments = new TreeSet<Deployment>(new Comparator<Deployment>() {
			@Override
			public int compare(Deployment o1, Deployment o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		orderedDeployments.addAll(dbInstallation.getDeployments());
		dbInstallation.setDeployments(orderedDeployments.descendingSet());	
		
		
		setCurrentObject(request, dbInstallation);
		return dbInstallation;
	}
}
