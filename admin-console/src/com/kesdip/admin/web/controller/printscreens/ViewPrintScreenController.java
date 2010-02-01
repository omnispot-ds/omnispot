/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.printscreens;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.beans.ViewPrintScreenBean;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.logic.PrintScreenLogic;
import com.kesdip.common.util.StringUtils;

/**
 * Controller for the print-screen viewing form.
 * 
 * @author gerogias
 */
public class ViewPrintScreenController extends BaseFormController {

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

		ViewPrintScreenBean bean = (ViewPrintScreenBean) super
				.formBackingObject(request);

		String siteId = request.getParameter("site.id");
		String groupId = request.getParameter("installationGroup.id");
		String customerId = request.getParameter("customer.id");

		if (!StringUtils.isEmpty(siteId)) {
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
		
		PrintScreenLogic logic = getLogicFactory().getPrintScreenLogic();
		bean = logic.getPrintScreens(bean);
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
