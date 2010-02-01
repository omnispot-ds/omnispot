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
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.kesdip.admin.web.controller.BaseFormController;
import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.logic.CustomerLogic;
import com.kesdip.business.logic.GroupLogic;

/**
 * Controller for the {@link InstallationGroup} creation form.
 * 
 * @author gerogias
 */
public class CreateController extends BaseFormController {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(CreateController.class);
	
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

		InstallationGroup group = (InstallationGroup) command;
		if (logger.isDebugEnabled()) {
			logger.debug("group has: " +  group.getInstallations().size()+ " instll");
		}
		GroupLogic logic = getLogicFactory().getGroupLogic();
		try {
			InstallationGroup dbGroup = logic.create(group);
			setCurrentObject(request, dbGroup);
		} catch (ValidationException ve) {
			return handleErrors(request, response, ve, group);
		} catch (Exception e) {
			return handleErrors(request, response, e);
		}
		return new ModelAndView(getSuccessView());
	}

	/**
	 * Initialize the internal Customer object from the DB.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		InstallationGroup group = (InstallationGroup) super.formBackingObject(request);

		try {
			Customer customer = new Customer();
			customer.setId(Long.valueOf(request.getParameter("customer.id")));
	
			CustomerLogic logic = getLogicFactory().getCustomerLogic();
			Customer dbCustomer = logic.getInstance(customer);
			group.setCustomer(dbCustomer);
			setCurrentObject(request, dbCustomer);
		} catch (Exception e) {
			// do nothing
		}
		return group;
	}

}
