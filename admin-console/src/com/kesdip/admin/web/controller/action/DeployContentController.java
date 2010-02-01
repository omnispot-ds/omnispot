/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.controller.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.domain.admin.generated.Content;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Deployment;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.ActionLogic;
import com.kesdip.business.logic.CustomerLogic;
import com.kesdip.business.logic.GroupLogic;
import com.kesdip.business.logic.InstallationLogic;
import com.kesdip.business.logic.SiteLogic;
import com.kesdip.common.util.StringUtils;

/**
 * Controller for the deployment of {@link Content}.
 * 
 * @author gerogias
 */
public class DeployContentController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Performs object creation.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param command
	 *            the DTO
	 * @param errors
	 *            the errors object
	 * @return ModelAndView the sucess or failure view
	 */
	@Override
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		ContentDeploymentBean bean = (ContentDeploymentBean) command;
		ActionLogic logic = getLogicFactory().getActionLogic();
		try {
			Deployment dbDeployment = logic.deployContent(bean);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, bean);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}

		// return the right success view
		if (bean.getInstallationGroup() != null) {
			return new ModelAndView(getAllSuccessViews().get(
					"installationGroup"));
		} else {
			return new ModelAndView(getAllSuccessViews().get("site"));
		}
	}

	/**
	 * Initialize the internal Customer object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		ContentDeploymentBean bean = (ContentDeploymentBean) super
				.formBackingObject(request);

		String installationId = request.getParameter("installation.id");
		String siteId = request.getParameter("site.id");
		String groupId = request.getParameter("installationGroup.id");
		String customerId = request.getParameter("customer.id");

		InstallationLogic installationLogic = getLogicFactory()
				.getInstallationLogic();
		if (!StringUtils.isEmpty(installationId)) {
			// installation
			Installation installation = new Installation();
			installation.setId(Long.valueOf(installationId));
			Installation dbInstallation = installationLogic
					.getInstance(installation);
			bean.setInstallation(dbInstallation);
			bean.setSite(dbInstallation.getSite());
			bean.setCustomer(dbInstallation.getSite().getCustomer());
			bean.setInstallationCount(1);
		} else if (!StringUtils.isEmpty(siteId)) {
			// site
			Site site = new Site();
			site.setId(Long.valueOf(siteId));
			SiteLogic logic = getLogicFactory().getSiteLogic();
			Site dbSite = logic.getInstance(site);
			bean.setSite(dbSite);
			bean.setCustomer(dbSite.getCustomer());
			bean.setInstallationCount(installationLogic
					.getInstallationCount(site));
		} else if (!StringUtils.isEmpty(groupId)) {
			// group
			InstallationGroup group = new InstallationGroup();
			group.setId(Long.valueOf(groupId));
			GroupLogic logic = getLogicFactory().getGroupLogic();
			InstallationGroup dbGroup = logic.getInstance(group);
			bean.setInstallationGroup(dbGroup);
			bean.setCustomer(dbGroup.getCustomer());
			bean.setInstallationCount(installationLogic
					.getInstallationCount(group));
		} else {
			// customer
			Customer customer = new Customer();
			customer.setId(Long.valueOf(customerId));
			CustomerLogic logic = getLogicFactory().getCustomerLogic();
			Customer dbCustomer = logic.getInstance(customer);
			bean.setCustomer(dbCustomer);
			bean.setInstallationCount(installationLogic
					.getInstallationCount(customer));
		}
		// set the right form view
		setFormViewInternal(bean);
		// set the right selected object
		if (bean.getInstallation() != null) {
			setCurrentObject(request, bean.getInstallation());
		} else if (bean.getSite() != null) {
			setCurrentObject(request, bean.getSite());
		} else if (bean.getInstallationGroup() != null) {
			setCurrentObject(request, bean.getInstallationGroup());
		} else {
			setCurrentObject(request, bean.getCustomer());
		}

		return bean;
	}

	/**
	 * Set the form view depending on whether we are dealing with a group or
	 * site/customer/installation.
	 * 
	 * @param bean
	 *            the current bean
	 */
	private final void setFormViewInternal(ContentDeploymentBean bean) {

		if (bean.getInstallationGroup() != null) {
			setFormView(getAllFormViews().get("installationGroup"));
		} else {
			setFormView(getAllFormViews().get("site"));
		}
	}

}
