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
import com.kesdip.business.beans.ActionBean;
import com.kesdip.business.domain.admin.generated.Action;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Parameter;
import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.ActionLogic;
import com.kesdip.business.logic.InstallationLogic;
import com.kesdip.common.util.StringUtils;

/**
 * Controller for simple Installation actions (start, stop, reboot, fetch-log).
 * 
 * @author gerogias
 */
public class SimpleActionController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the action parameter.
	 */
	private final static String PARAM_ACTION = "actionCommand";

	/**
	 * Flag to indicate the addition of a param.
	 */
	private final static String ADD_PARAM = "addParam";

	/**
	 * Flag to indicate the removal of a param.
	 */
	private final static String REMOVE_PARAM = "removeParam";

	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException exception)
			throws Exception {

		ActionBean bean = (ActionBean) command;

		ActionLogic logic = getLogicFactory().getActionLogic();
		try {
			logic.scheduleAction(bean);
		} catch (ValidationException ve) {
			return handleErrors(req, res, ve, bean);
		} catch (Exception e) {
			return handleErrors(req, res, e);
		}

		return new ModelAndView(getSuccessView());
	}

	/**
	 * Populate the backing object with the parent object. Decide on the type of
	 * action.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest req) throws Exception {
		ActionBean bean = (ActionBean) super.formBackingObject(req);

		if (isFormChangeRequest(req)) {
			if (bean.getInstallation() != null) { 
				setCurrentObject(req, bean.getInstallation());
			} else if (bean.getSite() != null) {
				setCurrentObject(req, bean.getSite());
			} else if (bean.getInstallationGroup() != null) {
				setCurrentObject(req, bean.getInstallationGroup());
			} else {
				setCurrentObject(req, bean.getCustomer());
			}
			return bean;
		}

		bean.setAction(new Action());
		// we need this trick to "fool" the Set and accept values
		Parameter temp = new Parameter();
		temp.setId(-1L);
		bean.getAction().getParameters().add(temp);

		String customerId = req.getParameter("customer.id");
		String groupId = req.getParameter("installationGroup.id");
		String siteId = req.getParameter("site.id");
		String installationId = req.getParameter("installation.id");

		// action type
		bean.getAction()
				.setType(Short.valueOf(req.getParameter("action.type")));

		InstallationLogic installationLogic = getLogicFactory()
				.getInstallationLogic();
		// determine parent object and set it as current
		if (!StringUtils.isEmpty(installationId)) {
			Installation installation = new Installation();
			installation.setId(Long.valueOf(installationId));
			Installation dbInstallation = getLogicFactory()
					.getInstallationLogic().getInstance(installation);
			bean.setInstallation(dbInstallation);
			bean.setSite(dbInstallation.getSite());
			bean.setCustomer(dbInstallation.getSite().getCustomer());
			setCurrentObject(req, dbInstallation);
			setSuccessView(getAllSuccessViews().get("site"));
			setFormView(getAllFormViews().get("site"));
			bean.setInstallationCount(1);
		} else if (!StringUtils.isEmpty(siteId)) {
			Site site = new Site();
			site.setId(Long.valueOf(siteId));
			Site dbSite = getLogicFactory().getSiteLogic().getInstance(site);
			bean.setSite(dbSite);
			bean.setCustomer(dbSite.getCustomer());
			setCurrentObject(req, site);
			setSuccessView(getAllSuccessViews().get("site"));
			setFormView(getAllFormViews().get("site"));
			bean.setInstallationCount(installationLogic
					.getInstallationCount(site));
		} else if (!StringUtils.isEmpty(groupId)) {
			InstallationGroup group = new InstallationGroup();
			group.setId(Long.valueOf(groupId));
			InstallationGroup dbGroup = getLogicFactory().getGroupLogic()
					.getInstance(group);
			bean.setInstallationGroup(dbGroup);
			bean.setCustomer(dbGroup.getCustomer());
			setCurrentObject(req, dbGroup);
			setSuccessView(getAllSuccessViews().get("installationGroup"));
			setFormView(getAllFormViews().get("installationGroup"));
			bean.setInstallationCount(installationLogic
					.getInstallationCount(group));
		} else {
			Customer customer = new Customer();
			customer.setId(Long.valueOf(customerId));
			Customer dbCustomer = getLogicFactory().getCustomerLogic()
					.getInstance(customer);
			bean.setCustomer(dbCustomer);
			setCurrentObject(req, dbCustomer);
			setSuccessView(getAllSuccessViews().get("site"));
			setFormView(getAllFormViews().get("site"));
			bean.setInstallationCount(installationLogic
					.getInstallationCount(customer));
		}

		return bean;
	}

	/**
	 * Modifies the session bean based on the value of the "action" parameter.
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onFormChange(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	protected void onFormChange(HttpServletRequest req,
			HttpServletResponse response, Object command) throws Exception {

		ActionBean bean = (ActionBean) command;
		if (isFormChangeRequest(req)) {
			if (bean.getInstallation() != null) { 
				setCurrentObject(req, bean.getInstallation());
			} else if (bean.getSite() != null) {
				setCurrentObject(req, bean.getSite());
			} else if (bean.getInstallationGroup() != null) {
				setCurrentObject(req, bean.getInstallationGroup());
			} else {
				setCurrentObject(req, bean.getCustomer());
			}
		}

		String action = req.getParameter(PARAM_ACTION);
		// add a work entry at the end
		// use the id to force-enter the object in the Set
		if (ADD_PARAM.equals(action)) {
			Parameter temp = new Parameter();
			temp.setId(bean.getAction().getParameters().size() + 1L);
			bean.getAction().getParameters().add(temp);
		} else if (REMOVE_PARAM.equals(action)) {
			// remove the last work entry
			Parameter temp = new Parameter();
			temp.setId((long)bean.getAction().getParameters().size());
			bean.getAction().getParameters().remove(temp);
		}
	}

	/**
	 * Determines if this a form change request. Checks request parameter
	 * "action".
	 * 
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#isFormChangeRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected boolean isFormChangeRequest(HttpServletRequest request) {
		return !StringUtils.isEmpty(request.getParameter(PARAM_ACTION));
	}

}
