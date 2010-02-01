/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 4, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.beans.ActionQueryBean;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.logic.ActionLogic;
import com.kesdip.common.util.StringUtils;

/**
 * Controller for the action viewing form.
 * 
 * @author gerogias
 */
public class ViewActionsController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A map with the form views.
	 */
	private Map<String, String> allFormViews = null;
	
	/**
	 * A map with the success views. 
	 */
	private Map<String, String> allSuccessViews = null;
	
	/**
	 * Creates the form-backing object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		ActionQueryBean bean = (ActionQueryBean) super
				.formBackingObject(request);

		String siteId = request.getParameter("site.id");
		String groupId = request.getParameter("installationGroup.id");
		String customerId = request.getParameter("customer.id");
		String installationId = request.getParameter("installation.id");

		if (!StringUtils.isEmpty(installationId)) {
			// installation
			Installation installation = new Installation();
			installation.setId(Long.valueOf(installationId));
			bean.setInstallation(installation);
			setFormView(allFormViews.get("installation"));
			setSuccessView(allSuccessViews.get("installation"));
			setCurrentObject(request, installation);
		} else if (!StringUtils.isEmpty(siteId)) {
			// site
			Site site = new Site();
			site.setId(Long.valueOf(siteId));
			bean.setSite(site);
			setFormView(allFormViews.get("site"));
			setSuccessView(allSuccessViews.get("site"));
			setCurrentObject(request, site);
		} else if (!StringUtils.isEmpty(groupId)) {
			// group
			InstallationGroup group = new InstallationGroup();
			group.setId(Long.valueOf(groupId));
			bean.setInstallationGroup(group);
			setFormView(allFormViews.get("installationGroup"));
			setSuccessView(allSuccessViews.get("installationGroup"));
			setCurrentObject(request, group);
		} else {
			// customer
			Customer customer = new Customer();
			customer.setId(Long.valueOf(customerId));
			bean.setCustomer(customer);
			setFormView(allFormViews.get("customer"));
			setSuccessView(allSuccessViews.get("customer"));
			setCurrentObject(request, customer);
		}
		
		ActionLogic logic = getLogicFactory().getActionLogic();
		bean = logic.getActionSet(bean);
		return bean;
	}

	/**
	 * @return the allFormViews
	 */
	public Map<String, String> getAllFormViews() {
		return allFormViews;
	}

	/**
	 * @param allFormViews the allFormViews to set
	 */
	public void setAllFormViews(Map<String, String> allFormViews) {
		this.allFormViews = allFormViews;
	}

	/**
	 * @return the allSuccessViews
	 */
	public Map<String, String> getAllSuccessViews() {
		return allSuccessViews;
	}

	/**
	 * @param allSuccessViews the allSuccessViews to set
	 */
	public void setAllSuccessViews(Map<String, String> allSuccessViews) {
		this.allSuccessViews = allSuccessViews;
	}
}
